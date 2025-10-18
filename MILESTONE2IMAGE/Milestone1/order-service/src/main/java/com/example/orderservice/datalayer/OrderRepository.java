package com.example.orderservice.datalayer;

import org.springframework.data.mongodb.repository.MongoRepository;
// No change needed here if Order.id is String, OrderIdentifier is not the document @Id
// If Order.id becomes ObjectId, then MongoRepository<Order, ObjectId>
public interface OrderRepository extends MongoRepository<Order, String> { // Assuming Order.id is String
    Order findByOrderIdentifierOrderId(String orderId);
}