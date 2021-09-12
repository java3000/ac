package ru.hilariousstartups.javaskills.psplayer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.hilariousstartups.javaskills.psplayer.swagger_codegen.ApiClient;
import ru.hilariousstartups.javaskills.psplayer.swagger_codegen.ApiException;
import ru.hilariousstartups.javaskills.psplayer.swagger_codegen.api.PerfectStoreEndpointApi;
import ru.hilariousstartups.javaskills.psplayer.swagger_codegen.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PerfectStorePlayer implements ApplicationListener<ApplicationReadyEvent> {

    private String serverUrl;

    //* 3 3 56 800 s = 1.8803106E7 / 1.64842E7 / 402780.0 / 1916126.0
    //* 3 3 65 800 s = 1.6643544E7 / 1.3953E7 / 402822.0 / 2287722.0
    //* 3 3 66 810 s = 1.6756676E7 / 1.431758E7 / 402829.0 / 2036267.0
    //* 3 3 66 810 s = 1.6680877E7 / 1.412385E7 / 402759.0 / 2154268.0

    int initialRacksRating = 3;
    int rackRating = 3;
    int overhead = 66; //56
    int initialQuantity = 810;

    HireEmployeeCommand.ExperienceEnum exp = HireEmployeeCommand.ExperienceEnum.SENIOR;

    List<Employee> setToLine = new ArrayList<>();

    public PerfectStorePlayer(@Value("${rs.endpoint:http://localhost:9080}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(serverUrl);

        PerfectStoreEndpointApi psApiClient = new PerfectStoreEndpointApi(apiClient);

        log.info("Игрок готов. Подключаемся к серверу..");
        awaitServer(psApiClient);

        log.info("Подключение к серверу успешно. Начинаем игру");
        try {
            CurrentWorldResponse currentWorldResponse = null;
            int cnt = 0;
            do {
                cnt += 1;
                if (cnt % 120 == 0) {
                    log.info("Пройден " + cnt + " тик");
                }

                if (currentWorldResponse == null) {
                    currentWorldResponse = psApiClient.loadWorld();
                }

                CurrentTickRequest request = new CurrentTickRequest();

                placePopularProductsOnRack(currentWorldResponse, request);
                putCachersOnLine(currentWorldResponse, request);
                hireFireEmployees(currentWorldResponse, request);

                currentWorldResponse = psApiClient.tick(request);
            }
            while (!currentWorldResponse.isGameOver());

            log.info("Я заработал " + (currentWorldResponse.getIncome() - currentWorldResponse.getSalaryCosts() - currentWorldResponse.getStockCosts()) + "руб.");
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void hireFireEmployees(CurrentWorldResponse currentWorldResponse, CurrentTickRequest request) {
        List<CheckoutLine> freeCasses = currentWorldResponse
                .getCheckoutLines()
                .stream()
                .filter(line -> line.getEmployeeId() == null)
                .collect(Collectors.toList());

        List<Employee> onLine = new ArrayList<>();

        for (CheckoutLine c : currentWorldResponse.getCheckoutLines()) {
            if (c.getEmployeeId() != null) {
                for (Employee e : currentWorldResponse.getEmployees()) {
                    if (c.getEmployeeId().equals(e.getId())) {
                        onLine.add(e);
                    }
                }
            }
        }
        List<Employee> notOnLine = currentWorldResponse.getEmployees().stream().filter(x -> !onLine.contains(x)).collect(Collectors.toList());

        if (!notOnLine.isEmpty()) {
            if (!freeCasses.isEmpty()) {
                List<FireEmployeeCommand> fireEmployeeCommands = new ArrayList<>();
                for (Employee employee : notOnLine) {
                    FireEmployeeCommand fireEmployeeCommand = new FireEmployeeCommand();
                    fireEmployeeCommand.setEmployeeId(employee.getId());
                    fireEmployeeCommands.add(fireEmployeeCommand);
                }
                request.setFireEmployeeCommands(fireEmployeeCommands);
            }
        }

        if (!freeCasses.isEmpty()) {
            List<HireEmployeeCommand> hireEmployeeCommands = new ArrayList<>();
            for (CheckoutLine freeCass : freeCasses) {
                HireEmployeeCommand hireEmployeeCommand = new HireEmployeeCommand();
                hireEmployeeCommand.setExperience(exp);
                hireEmployeeCommand.setCheckoutLineId(freeCass.getId());
                hireEmployeeCommands.add(hireEmployeeCommand);
            }
            request.setHireEmployeeCommands(hireEmployeeCommands);
        }
    }

    private void placeExpensiveProductsOnRack(CurrentWorldResponse currentWorldResponse, CurrentTickRequest request) {
        ArrayList<BuyStockCommand> buyStockCommands = new ArrayList<>();
        ArrayList<PutOnRackCellCommand> putOnRackCellCommands = new ArrayList<>();
        List<Product> productsInStock = currentWorldResponse.getStock().stream()
                .sorted(Comparator.comparing(Product::getStockPrice))
                .collect(Collectors.toList());
        Collections.reverse(productsInStock);

        List<RackCell> racks = currentWorldResponse.getRackCells().stream()
                .filter(rack -> (rack.getProductId() == null || rack.getProductQuantity().equals(0)) && rack.getVisibility() > initialRacksRating)
                .sorted(Comparator.comparing(RackCell::getVisibility))
                .collect(Collectors.toList());
        Collections.reverse(racks);

        for (int i = 0; i < Math.min(racks.size(), productsInStock.size()); i++) {
            RackCell rack = racks.get(i);
            Product producttoPutOnRack = productsInStock.get(i);

            Integer productQuantity = rack.getProductQuantity();
            if (productQuantity == null) {
                productQuantity = 0;
            }

            Integer orderQuantity = rack.getCapacity() - productQuantity;
            if (orderQuantity.equals(rack.getCapacity())) {
                if (producttoPutOnRack.getInStock() < orderQuantity) {
                    BuyStockCommand command = new BuyStockCommand();
                    command.setProductId(producttoPutOnRack.getId());
                    command.setQuantity(initialQuantity);
                    buyStockCommands.add(command);
                }
                PutOnRackCellCommand command2 = new PutOnRackCellCommand();
                command2.setProductId(producttoPutOnRack.getId());
                command2.setRackCellId(rack.getId());
                command2.setProductQuantity(rack.getCapacity());
                command2.setSellPrice(producttoPutOnRack.getStockPrice() + overhead);
                putOnRackCellCommands.add(command2);
            }
        }

        request.setBuyStockCommands(buyStockCommands);
        request.setPutOnRackCellCommands(putOnRackCellCommands);
    }

    private void placePopularProductsOnRack(CurrentWorldResponse currentWorldResponse, CurrentTickRequest request) {

        log.info("enter placePopularProductsOnRack");

        List<ProductInBasket> popularProducts = new ArrayList<>();

        for (Customer x : currentWorldResponse.getCustomers().stream()
                .filter(customer -> customer.getMode().equals(Customer.ModeEnum.AT_CHECKOUT))
                .collect(Collectors.toList())) {
            popularProducts.addAll(x.getBasket());
        }

        popularProducts = popularProducts.stream().distinct().sorted(Comparator.comparing(ProductInBasket::getPrie)).collect(Collectors.toList());

        if (popularProducts.size() == 0) {
            placeExpensiveProductsOnRack(currentWorldResponse, request);
            return;
        }

        Collections.reverse(popularProducts);

        List<Product> stock = currentWorldResponse.getStock();
        List<BuyStockCommand> buyPopularProducts = new ArrayList<>();
        List<PutOnRackCellCommand> putPopularOnRack = new ArrayList<>();
        List<RackCell> racks = currentWorldResponse.getRackCells().stream()
                .filter(rack -> (rack.getProductId() == null || rack.getProductQuantity().equals(0)) && rack.getVisibility() > rackRating)
                .sorted(Comparator.comparing(RackCell::getVisibility))
                .collect(Collectors.toList());
        Collections.reverse(racks);

        for (int i = 0; i < Math.min(racks.size(), popularProducts.size()); i++) {
            RackCell rack = racks.get(i);

            Integer productQuantity = rack.getProductQuantity();
            if (productQuantity == null) {
                productQuantity = 0;
            }

            Integer orderQuantity = rack.getCapacity() - productQuantity;

            List<ProductInBasket> finalSortedPopularProducts = popularProducts;
            int finalI = i;
            Product x = stock.stream()
                    .filter(product -> product.getId().equals(finalSortedPopularProducts.get(finalI).getId()))
                    .findFirst()
                    .orElse(null);

            if (orderQuantity.equals(rack.getCapacity())) {
                if (x != null) {
                    if (x.getInStock() < orderQuantity) {
                        BuyStockCommand command = new BuyStockCommand();
                        command.setProductId(x.getId());
                        command.setQuantity(initialQuantity);
                        buyPopularProducts.add(command);
                    }
                    PutOnRackCellCommand command2 = new PutOnRackCellCommand();
                    command2.setProductId(x.getId());
                    command2.setRackCellId(rack.getId());
                    command2.setProductQuantity(rack.getCapacity());
                    command2.sellPrice(popularProducts.get(i).getPrie());
                    putPopularOnRack.add(command2);
                }
            }
        }
        request.setBuyStockCommands(buyPopularProducts);
        request.setPutOnRackCellCommands(putPopularOnRack);
    }

    private void putCachersOnLine(CurrentWorldResponse currentWorldResponse, CurrentTickRequest request) {

        log.info("enter putCachiersOnLine");

        List<CheckoutLine> freeCasses = currentWorldResponse
                .getCheckoutLines()
                .stream()
                .filter(line -> line.getEmployeeId() == null)
                .collect(Collectors.toList());

        List<Employee> onLine = new ArrayList<>();

        for (CheckoutLine c : currentWorldResponse.getCheckoutLines()) {
            if (c.getEmployeeId() != null) {
                for (Employee e : currentWorldResponse.getEmployees()) {
                    if (c.getEmployeeId().equals(e.getId())) {
                        onLine.add(e);
                    }
                }
            }
        }

        List<Employee> notOnLine = currentWorldResponse.getEmployees().stream().filter(x -> !onLine.contains(x)).collect(Collectors.toList());
        if (!setToLine.isEmpty())
            notOnLine = notOnLine.stream().filter(x -> !setToLine.contains(x)).collect(Collectors.toList());
        setToLine.clear();

        if (freeCasses.size() > 0 && notOnLine.size() > 0) {
            List<SetOnCheckoutLineCommand> toCheckoutLineCommands = new ArrayList<>();
            for (int i = 0; i < freeCasses.size(); i++) {
                SetOnCheckoutLineCommand setOnCheckoutLineCommand = new SetOnCheckoutLineCommand();
                setOnCheckoutLineCommand.setEmployeeId(notOnLine.get(i).getId());
                setOnCheckoutLineCommand.setCheckoutLineId(freeCasses.get(i).getId());
                toCheckoutLineCommands.add(setOnCheckoutLineCommand);

                if (!setToLine.contains(notOnLine.get(i))) setToLine.add(notOnLine.get(i));
            }
            request.setOnCheckoutLineCommands(toCheckoutLineCommands);
        }
    }

    private void awaitServer(PerfectStoreEndpointApi psApiClient) {
        int awaitTimes = 60;
        int cnt = 0;
        boolean serverReady = false;
        do {
            try {
                cnt += 1;
                psApiClient.loadWorld();
                serverReady = true;

            } catch (ApiException e) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException interruptedException) {
                    e.printStackTrace();
                }
            }
        } while (!serverReady && cnt < awaitTimes);
    }
}