/*
 * OpenAPI definition
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: v0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ru.hilariousstartups.javaskills.psplayer.swagger_codegen.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * Команда на отправку сотрудника на кассу.
 */
@Schema(description = "Команда на отправку сотрудника на кассу.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-08-29T20:10:20.018547+03:00[Europe/Moscow]")
public class SetOnCheckoutLineCommand {
  @SerializedName("checkoutLineId")
  private Integer checkoutLineId = null;

  @SerializedName("employeeId")
  private Integer employeeId = null;

  public SetOnCheckoutLineCommand checkoutLineId(Integer checkoutLineId) {
    this.checkoutLineId = checkoutLineId;
    return this;
  }

   /**
   * Номер кассы
   * @return checkoutLineId
  **/
  @Schema(required = true, description = "Номер кассы")
  public Integer getCheckoutLineId() {
    return checkoutLineId;
  }

  public void setCheckoutLineId(Integer checkoutLineId) {
    this.checkoutLineId = checkoutLineId;
  }

  public SetOnCheckoutLineCommand employeeId(Integer employeeId) {
    this.employeeId = employeeId;
    return this;
  }

   /**
   * Id сотрудника
   * @return employeeId
  **/
  @Schema(required = true, description = "Id сотрудника")
  public Integer getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SetOnCheckoutLineCommand setOnCheckoutLineCommand = (SetOnCheckoutLineCommand) o;
    return Objects.equals(this.checkoutLineId, setOnCheckoutLineCommand.checkoutLineId) &&
        Objects.equals(this.employeeId, setOnCheckoutLineCommand.employeeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(checkoutLineId, employeeId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SetOnCheckoutLineCommand {\n");
    
    sb.append("    checkoutLineId: ").append(toIndentedString(checkoutLineId)).append("\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
