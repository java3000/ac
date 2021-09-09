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
 * Кадровое агенство. Справочная информация о том, каких сотрудников можно нанять и по какой ставке
 */
@Schema(description = "Кадровое агенство. Справочная информация о том, каких сотрудников можно нанять и по какой ставке")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-08-29T20:10:20.018547+03:00[Europe/Moscow]")
public class EmployeeRecruitmentOffer {
  @SerializedName("employeeType")
  private String employeeType = null;

  @SerializedName("experience")
  private String experience = null;

  @SerializedName("salary")
  private Integer salary = null;

  public EmployeeRecruitmentOffer employeeType(String employeeType) {
    this.employeeType = employeeType;
    return this;
  }

   /**
   * Get employeeType
   * @return employeeType
  **/
  @Schema(description = "")
  public String getEmployeeType() {
    return employeeType;
  }

  public void setEmployeeType(String employeeType) {
    this.employeeType = employeeType;
  }

  public EmployeeRecruitmentOffer experience(String experience) {
    this.experience = experience;
    return this;
  }

   /**
   * Get experience
   * @return experience
  **/
  @Schema(description = "")
  public String getExperience() {
    return experience;
  }

  public void setExperience(String experience) {
    this.experience = experience;
  }

  public EmployeeRecruitmentOffer salary(Integer salary) {
    this.salary = salary;
    return this;
  }

   /**
   * Get salary
   * @return salary
  **/
  @Schema(description = "")
  public Integer getSalary() {
    return salary;
  }

  public void setSalary(Integer salary) {
    this.salary = salary;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeRecruitmentOffer employeeRecruitmentOffer = (EmployeeRecruitmentOffer) o;
    return Objects.equals(this.employeeType, employeeRecruitmentOffer.employeeType) &&
        Objects.equals(this.experience, employeeRecruitmentOffer.experience) &&
        Objects.equals(this.salary, employeeRecruitmentOffer.salary);
  }

  @Override
  public int hashCode() {
    return Objects.hash(employeeType, experience, salary);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmployeeRecruitmentOffer {\n");
    
    sb.append("    employeeType: ").append(toIndentedString(employeeType)).append("\n");
    sb.append("    experience: ").append(toIndentedString(experience)).append("\n");
    sb.append("    salary: ").append(toIndentedString(salary)).append("\n");
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