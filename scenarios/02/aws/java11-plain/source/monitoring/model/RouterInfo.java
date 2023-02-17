package com.monitoring.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "monitoring")
@Data
@NoArgsConstructor
public class RouterInfo {

    @DynamoDBHashKey(attributeName = "date")
    private String pk;
    @DynamoDBAttribute(attributeName = "from")
    private String name;
    @DynamoDBAttribute(attributeName = "routerIp")
    private String ip;
    @DynamoDBAttribute(attributeName = "timeOutMS")
    private int timeOutMS;
    @DynamoDBAttribute(attributeName = "pingProvider")
    private String pingProvider;

    public RouterInfo(String pk) {
        this.pk = pk;
    }
}
