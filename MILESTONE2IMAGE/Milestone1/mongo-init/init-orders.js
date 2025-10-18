// mongo-init/init-orders.js
db = db.getSiblingDB('orderdb');
db.orders.drop();
db.createCollection('orders');
db.orders.insertMany([
    {
        orderIdentifier: { orderId: "g1d2b3a4-6b7c-890d-1e2f-3a4b5c6d7e8f" },
        customerId: "1",
        paymentId: "PMT-001",
        productIds: ["p001", "p002"], // <-- Store string product IDs
        createdAt: new Date("2024-05-10T10:00:00Z"),
        status: "PROCESSING",
        amount: 34.50
    },
    {
        orderIdentifier: { orderId: "g2d3b4a5-6b7c-890d-1e2f-3a4b5c6d7e8f" },
        customerId: "2",
        paymentId: "PMT-002",
        productIds: ["p003", "p004"], // <-- Store string product IDs
        createdAt: new Date("2024-05-11T11:30:00Z"),
        status: "COMPLETED",
        amount: 82.90
    },
    {
        orderIdentifier: { orderId: "g3d4b5a6-6b7c-890d-1e2f-3a4b5c6d7e8f" },
        customerId: "3",
        paymentId: "PMT-003",
        productIds: ["p005"], // <-- Store string product IDs
        createdAt: new Date("2024-05-12T14:15:00Z"),
        status: "DELIVERED",
        amount: 59.99
    },
    {
        orderIdentifier: { orderId: "g4d5b6a7-6b7c-890d-1e2f-3a4b5c6d7e8f" },
        customerId: "4",
        paymentId: "PMT-004",
        productIds: ["p001"], // <-- Store string product IDs
        createdAt: new Date("2024-05-13T09:05:00Z"),
        status: "CANCELLED",
        amount: 45.20
    },
    {
        orderIdentifier: { orderId: "g5d6b7a8-6b7c-890d-1e2f-3a4b5c6d7e8f" },
        customerId: "5",
        paymentId: "PMT-005",
        productIds: ["p002", "p003", "p004"], // <-- Store string product IDs
        createdAt: new Date("2024-05-14T16:45:00Z"),
        status: "PROCESSING",
        amount: 120.00
    }
]);
print("SUCCESS: 'orderdb' database and 'orders' collection initialized with STRING productIds.");