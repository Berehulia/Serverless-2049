package com.monitoring.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "monitoring")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Monitoring {

    @DynamoDBHashKey(attributeName = "date")
    private String date;
    @DynamoDBRangeKey(attributeName = "from")
    private String from;
    @DynamoDBAttribute(attributeName = "to")
    private String to;
    @DynamoDBAttribute(attributeName = "status")
    private String status;
    @DynamoDBAttribute(attributeName = "checksCount")
    private int checksCount;
    @DynamoDBAttribute(attributeName = "routerName")
    private String routerName;


    public Monitoring(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(from.substring(from.indexOf(' ') + 1))
                .append(" - ")
                .append(to.substring(to.indexOf(' ') + 1))
                .append(' ')
                .append(status)
                .append("\tchecks=")
                .append(checksCount)
                .toString();
    }
}
