#!/bin/bash

# Base URL for the API
BASE_URL="http://localhost:8080/api/v1"

# Function to make requests and display output
make_request() {
    local method="$1"
    local endpoint="$2"
    local data="$3"
    local description="$4"

    echo "----------------------------------------------------"
    echo "Executing: $description"
    echo "Request: $method $BASE_URL$endpoint"

    if [ -n "$data" ]; then
        echo "Data: $data"
        response=$(curl -s -w "\nHTTP_STATUS_CODE:%{http_code}\n" -X "$method" -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\nHTTP_STATUS_CODE:%{http_code}\n" -X "$method" "$BASE_URL$endpoint")
    fi

    http_status_code=$(echo "$response" | grep "HTTP_STATUS_CODE:" | cut -d':' -f2)
    body=$(echo "$response" | sed '$d') # Remove last line (status code)

    echo "Response Body:"
    echo "$body"
    echo "Status Code: $http_status_code"
    echo "----------------------------------------------------"
    echo ""
    sleep 1 # Adding a small delay between requests
}

echo "Starting API Endpoint Execution..."

# --- Customers ---
echo "--- Testing Customer Endpoints ---"

# IDs for specific customer operations
CUSTOMER_ID_FOR_GET_UPDATE_DELETE="1" # Used for GET by ID, PUT, and DELETE

# GET REQUEST CUSTOMERS
make_request "GET" "/customers" "" "Get all customers"

# GET REQUEST CUSTOMER BY ID
make_request "GET" "/customers/$CUSTOMER_ID_FOR_GET_UPDATE_DELETE" "" "Get customer by ID $CUSTOMER_ID_FOR_GET_UPDATE_DELETE"

# POST REQUEST CUSTOMERS
CUSTOMER_POST_DATA='{
  "firstName": "Jane",
  "lastName": "Doe",
  "emailAddress": "jane.doe@example.com",
  "streetAddress": "456 Oak St",
  "city": "Othertown",
  "province": "Otherstate",
  "country": "USA",
  "postalCode": "67890"
}'
make_request "POST" "/customers" "$CUSTOMER_POST_DATA" "Create a new customer"
# If you want to use the ID of the newly created customer for subsequent GET/PUT/DELETE,
# you would need to parse the response from this POST request.
# For now, we're using a predefined ID.

# PUT REQUEST CUSTOMERS
CUSTOMER_PUT_DATA='{
  "firstName": "Jane D.",
  "lastName": "Doe-Smith",
  "emailAddress": "jane.doe.smith@example.com",
  "streetAddress": "123 New Avenue",
  "city": "UpdatedCity",
  "province": "UpdatedState",
  "country": "USA",
  "postalCode": "54321"
}'
make_request "PUT" "/customers/$CUSTOMER_ID_FOR_GET_UPDATE_DELETE" "$CUSTOMER_PUT_DATA" "Update customer with ID $CUSTOMER_ID_FOR_GET_UPDATE_DELETE"

# DELETE REQUEST CUSTOMERS
make_request "DELETE" "/customers/$CUSTOMER_ID_FOR_GET_UPDATE_DELETE" "" "Delete customer with ID $CUSTOMER_ID_FOR_GET_UPDATE_DELETE"


# --- Orders ---
echo "--- Testing Order Endpoints ---"

# IDs for specific order operations
ORDER_ID_FOR_GET_UPDATE_DELETE="g1d2b3a4-6b7c-890d-1e2f-3a4b5c6d7e8f" # Used for GET by ID, PUT, and DELETE

# GET REQUEST ORDERS
make_request "GET" "/orders" "" "Get all orders"

# GET REQUEST ORDER BY ID
make_request "GET" "/orders/$ORDER_ID_FOR_GET_UPDATE_DELETE" "" "Get order by ID $ORDER_ID_FOR_GET_UPDATE_DELETE"

# POST REQUEST ORDERS (Standard)
# Assuming customerId "6" exists, and productIds "p001", "p005" exist.
ORDER_POST_DATA='{
    "customerId": "6",
    "paymentId": "PMT-006",
    "productIds": [
        "p003",
        "p005"
    ],
    "status": "RECEIVED",
    "amount": 8.98
}'
make_request "POST" "/orders" "$ORDER_POST_DATA" "Create a new order (Standard)"

# POST REQUEST ORDERS (Aggregate Invariable - amount mismatch test)
# Assuming customerId "6" exists, and productIds "p003", "p004" exist.
# The amount 12.50 is intentionally set to test the API's behavior when it might not match the sum of product prices.
ORDER_POST_AGGREGATE_DATA='{
    "customerId": "6",
    "paymentId": "PMT-006",
    "productIds": [
        "p003",
        "p004"
    ],
    "status": "PROCESSING",
    "amount": 12.50
}'
make_request "POST" "/orders" "$ORDER_POST_AGGREGATE_DATA" "Create an order (Aggregate Invariable - amount mismatch test)"


# PUT REQUEST ORDERS
# Assuming productIds "p003", "p004" exist for order $ORDER_ID_FOR_GET_UPDATE_DELETE.
ORDER_PUT_DATA='{
    "customerId": "6",
    "paymentId": "PMT-006",
    "productIds": [
        "p003",
        "p004"
    ],
    "status": "PROCESSING",
    "amount": 6.49
}'
make_request "PUT" "/orders/$ORDER_ID_FOR_GET_UPDATE_DELETE" "$ORDER_PUT_DATA" "Update order with ID $ORDER_ID_FOR_GET_UPDATE_DELETE"

# DELETE REQUEST ORDER
make_request "DELETE" "/orders/$ORDER_ID_FOR_GET_UPDATE_DELETE" "" "Delete order with ID $ORDER_ID_FOR_GET_UPDATE_DELETE"


# --- Payments ---
echo "--- Testing Payment Endpoints ---"

# IDs for specific payment operations
PAYMENT_ID_FOR_GET_UPDATE="PMT-003" # Used for GET by ID and PUT
PAYMENT_ID_FOR_DELETE="PMT-002"     # Used for DELETE

# GET REQUEST PAYMENTS
make_request "GET" "/payments" "" "Get all payments"

# GET REQUEST PAYMENT BY ID
make_request "GET" "/payments/$PAYMENT_ID_FOR_GET_UPDATE" "" "Get payment by ID $PAYMENT_ID_FOR_GET_UPDATE"

# POST REQUEST PAYMENTS
PAYMENT_POST_DATA='{
  "paymentId": "pay_sample_123",
  "amount": "50.00",
  "method": "Credit Card",
  "currency": "USD",
  "paymentDate": "2025-05-15"
}'
make_request "POST" "/payments" "$PAYMENT_POST_DATA" "Create a new payment"

# PUT REQUEST PAYMENTS
PAYMENT_PUT_DATA='{
  "paymentId": "PMT-003",
  "amount": "125.50",
  "method": "PayPal",
  "currency": "CAD",
  "paymentDate": "2025-05-20"
}'
make_request "PUT" "/payments/$PAYMENT_ID_FOR_GET_UPDATE" "$PAYMENT_PUT_DATA" "Update payment with ID $PAYMENT_ID_FOR_GET_UPDATE"

# DELETE REQUEST PAYMENTS
make_request "DELETE" "/payments/$PAYMENT_ID_FOR_DELETE" "" "Delete payment with ID $PAYMENT_ID_FOR_DELETE"


# --- Products ---
echo "--- Testing Product Endpoints ---"

# IDs for specific product operations
PRODUCT_ID_FOR_GET_UPDATE_DELETE="p001" # Used for GET by ID, PUT, and DELETE

# GET REQUEST PRODUCTS
make_request "GET" "/products" "" "Get all products"

# GET REQUEST PRODUCT BY ID
make_request "GET" "/products/$PRODUCT_ID_FOR_GET_UPDATE_DELETE" "" "Get product by ID $PRODUCT_ID_FOR_GET_UPDATE_DELETE"

# POST REQUEST PRODUCTS
PRODUCT_POST_DATA='{
  "name": "Sample Product",
  "description": "This is a sample product description.",
  "price": 29.99,
  "stockQuantity": 100
}'
make_request "POST" "/products" "$PRODUCT_POST_DATA" "Create a new product"

# PUT REQUEST PRODUCTS
PRODUCT_PUT_DATA='{
  "name": "Updated Sample Product",
  "description": "This is an updated description for the sample product.",
  "price": 32.50,
  "stockQuantity": 95
}'
make_request "PUT" "/products/$PRODUCT_ID_FOR_GET_UPDATE_DELETE" "$PRODUCT_PUT_DATA" "Update product with ID $PRODUCT_ID_FOR_GET_UPDATE_DELETE"

# DELETE REQUEST PRODUCTS
make_request "DELETE" "/products/$PRODUCT_ID_FOR_GET_UPDATE_DELETE" "" "Delete product with ID $PRODUCT_ID_FOR_GET_UPDATE_DELETE"

echo "--- API Endpoint Execution Finished ---"