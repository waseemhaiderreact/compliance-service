package com.alsharqi.compliance.api.requests;

import java.util.ArrayList;
import java.util.List;

public class ComplianceListFilterRequest {
    private List<FilterField> filterFieldList = new ArrayList<FilterField>();

    public ComplianceListFilterRequest() {
        filterFieldList = new ArrayList<FilterField>();
    }

    public ComplianceListFilterRequest(List<FilterField> filterFieldList) {
        this.filterFieldList = filterFieldList;
    }

    public static class FilterField {
        public String fieldName;
        public String compType;
        public String value1;
        public String value2;
        public String value3; //adding 3rd value for storing relative dates count
        public String compTypeDate; // adding comparison type for storing relative date
        public String fieldType;

        public FilterField() {
            fieldName = null;
            compType = null;
            value1 = null;
            value2 = null;
            value3 = null;
            fieldType = null;
        }

        public FilterField(String fieldName, String compType, String value1, String value2,String value3,String compTypeDate,String fieldType) {
            this.fieldName = fieldName;
            this.compType = compType;
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
            this.compTypeDate = compTypeDate;
            this.fieldType = fieldType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getCompType() {
            return compType;
        }

        public void setCompType(String compType) {
            this.compType = compType;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) { this.fieldType = fieldType; }

        public String getValue3() { return value3; }

        public void setValue3(String value3) { this.value3 = value3; }

        public String getCompTypeDate() {
            return compTypeDate;
        }

        public void setCompTypeDate(String compTypeDate) {
            this.compTypeDate = compTypeDate;
        }
    }

    public List<FilterField> getFilterFieldList() {
        return filterFieldList;
    }

    public void setFilterFieldList(List<FilterField> filterFieldList) {
        this.filterFieldList = filterFieldList;
    }
}
