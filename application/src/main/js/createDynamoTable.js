var params = {
    TableName: 'Customer',
    KeySchema: [{ AttributeName: 'Id', KeyType: 'HASH' }],
    AttributeDefinitions: [
        {  AttributeName: 'Id', AttributeType: 'S', },
    ],
    ProvisionedThroughput: { ReadCapacityUnits: 1, WriteCapacityUnits: 1, }
};

dynamodb.createTable(params, function(err, data) {
    if (err) ppJson(err);
    else ppJson(data);

});