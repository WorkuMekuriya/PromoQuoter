# PromoQuoter — Cart Pricing & Reservation Microservice

A Spring Boot REST service that calculates cart prices with pluggable promotion rules and reserves inventory on confirmation.

## Tech Stack

- **Java 17+**
- **Spring Boot 3.2**
- **Spring Web**, **Spring Data JPA**, **Validation**
- **H2** (in-memory database)

## Assumptions

1. **Product IDs**: Numeric (Long); create products first to obtain IDs for cart requests.
2. **Category discounts** apply before **Buy X Get Y** promotions (controlled by promotion `priority`).
3. **Customer segment** is accepted but not yet used for differential pricing (extensible).
4. **Idempotency**: When `Idempotency-Key` header is provided, duplicate confirms return the same order without re-reserving stock.
5. **Rounding**: All prices use `HALF_UP` to 2 decimal places; prices never go negative.
6. **Stock**: Pessimistic locking ensures safe concurrent cart confirmation.

## How to Run

```bash
# Using Maven
mvn spring-boot:run

# Or using Gradle (if gradlew exists)
./gradlew bootRun
```

The API runs at `http://localhost:8080`.

## Sample cURL Requests

### 1. Create Products

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '[
    {
      "name": "Laptop",
      "category": "ELECTRONICS",
      "price": 999.99,
      "stock": 50
    },
    {
      "name": "T-Shirt",
      "category": "CLOTHING",
      "price": 29.99,
      "stock": 100
    }
  ]'
```

### 2. Create Promotions

```bash
# 10% off ELECTRONICS (priority 0 = applied first)
curl -X POST http://localhost:8080/promotions \
  -H "Content-Type: application/json" \
  -d '[
    {
      "type": "PERCENT_OFF_CATEGORY",
      "category": "ELECTRONICS",
      "percentageOff": 10,
      "priority": 0
    }
  ]'

# Buy 2 Get 1 Free for product ID 1 (priority 1)
curl -X POST http://localhost:8080/promotions \
  -H "Content-Type: application/json" \
  -d '[
    {
      "type": "BUY_X_GET_Y",
      "productId": 1,
      "buyQuantity": 2,
      "getFreeQuantity": 1,
      "priority": 1
    }
  ]'

# Tiered bulk discount: 5+ units = 10% off (priority 2)
curl -X POST http://localhost:8080/promotions \
  -H "Content-Type: application/json" \
  -d '[
    {
      "type": "TIERED_BULK_DISCOUNT",
      "productId": 1,
      "minQuantity": 5,
      "percentageOff": 10,
      "priority": 2
    }
  ]'

# Shipping waiver: Free $5.99 shipping on orders over $50 (priority 3)
curl -X POST http://localhost:8080/promotions \
  -H "Content-Type: application/json" \
  -d '[
    {
      "type": "SHIPPING_WAIVER",
      "minOrderAmount": 50,
      "waiverAmount": 5.99,
      "priority": 3
    }
  ]'
```

### 3. Get Cart Quote

```bash
curl -X POST http://localhost:8080/cart/quote \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      { "productId": 1, "qty": 3 },
      { "productId": 2, "qty": 2 }
    ],
    "customerSegment": "REGULAR"
  }'
```

**Example response:**
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "Laptop",
      "quantity": 3,
      "unitPrice": 999.99,
      "subtotal": 2999.97,
      "discount": 599.99,
      "finalPrice": 2399.98,
      "appliedPromotions": "PERCENT_OFF_CATEGORY 10% off; BUY_X_GET_Y: Buy 2 Get 1 free"
    },
    ...
  ],
  "subtotal": 3059.95,
  "totalDiscount": 599.99,
  "total": 2459.96,
  "appliedPromotionsInOrder": ["PERCENT_OFF_CATEGORY: 10% off ELECTRONICS", "BUY_X_GET_Y: ..."]
}
```

### 4. Confirm Cart (Reserve Stock)

```bash
curl -X POST http://localhost:8080/cart/confirm \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: my-unique-key-123" \
  -d '{
    "items": [
      { "productId": 1, "qty": 2 },
      { "productId": 2, "qty": 1 }
    ],
    "customerSegment": "REGULAR"
  }'
```

**Example response:**
```json
{
  "orderId": "ORD-A1B2C3D4",
  "totalPrice": 1029.98
}
```
### Error Responses

- **409 CONFLICT** — Insufficient stock
- **404 NOT FOUND** — Product not found
- **400 BAD REQUEST** — Validation errors (e.g., missing fields, invalid values)

## Tests & Coverage

```bash
mvn test
mvn jacoco:report  # Report in target/site/jacoco/index.html
```

- Unit tests for promotion rules
- Integration tests for controllers (products, promotions, cart quote, cart confirm)
- Idempotency and insufficient-stock scenarios covered
