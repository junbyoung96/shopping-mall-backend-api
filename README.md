## Allra back-end-assignment 최준병



## API 명세

### 1. Products

#### 1.1 **GET** `/api/products`
구매할 수 있는 모든 상품을 조회합니다.
- **Request Param**
  - `page` : 조회할 페이지 번호 **(기본값: 1)**
  - `showCount` : 페이지당 표시할 상품 수 **(기본값: 20)**
- **Response**
  - **Http Code:** `Ok`
  ```json
  {    
    "products": [
      {
        "id": 1,
        "name": "String",
        "description": "String",
        "price" : "Number",
        "stock": "Number",
        "createdAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "updatedAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
      },
      ...
    ],
    "currentPage": "Number",
    "showCount": "Number",
    "totalPage": "Number"
  }

### 2. Carts

#### 2.1 **POST** `/api/carts`
특정 상품을 장바구니에 추가합니다.
- **Request Body**
```json
 {
    "userId" : "Number",
    "productId": "Number",
    "quantity": "Number"
 }
```
- **[Success]**  
  - **Http Code:** `Created`
  - **Response Body**
  ```json
    {
        "id": "{cartItemId}",
        "quantity": "{quantity}",
        "createdAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "updatedAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```
- **[Failed]**
    - **Case:** 상품의 재고보다 많은 수량을 선택한 경우
    - **Http Code:** `Conflict`
    - **Response Body**
    ```json
    {
        "type": "about:blank",
        "title": "Conflict",
        "status": 409,
        "detail": "선택하신 수량이 상품의 재고보다 많아 선택할 수 없습니다.",
        "instance": "/api/carts",
        "timestamp": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```

#### 2.2 **PATCH** `/api/api/carts/{cartItemId}`
장바구니의 특정 상품의 수량을 변경합니다.
- **Request Body**
```json
{
    "userId" : "Number",
    "quantity": "Number"
}
```
- **[Success]**
    - **Http Code:** `Ok`
    - **Response Body**
    ```json
    {
        "id": "{cartItemId}",
        "quantity": "{updatedQuantity}",
        "createdAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "updatedAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```
- **[Failed]**
    - **Case:** 상품의 재고보다 많은 수량을 선택한 경우
    - **Http Code:** `Conflict`
    - **Response Body**
    ```json
    {
        "type": "about:blank",
        "title": "Conflict",
        "status": 409,
        "detail": "선택하신 수량이 상품의 재고보다 많아 선택할 수 없습니다.",
        "instance": "/api/carts/{cartItemId}",
        "timestamp": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```
    - **Case:** 요청자와 리소스 소유자가 일치하지 않는경우
    - **Http Code:** `Forbidden`
    - **Response Body**
    ```json
    {
        "type": "about:blank",
        "title": "Forbidden",
        "status": 403,
        "detail": "리소스에 대한 권한이 없습니다.",
        "instance": "/api/carts/{cartItemId}",
        "timestamp": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```

#### 2.3 **DELETE** `/api/carts/{cartItemId}`
장바구니에서 특정 상품을 제거합니다.
- **Request Body**
```json
 {
    "userId" : "Number"
 }
```
- **[Success]**
    - **Http Code:** `No Content`

- **[Failed]**    
    - **Case:** 요청자와 리소스 소유자가 일치하지 않는경우
    - **Http Code:** `Forbidden`
    - **Response Body**
    ```json
    {
        "type": "about:blank",
        "title": "Forbidden",
        "status": 403,
        "detail": "리소스에 대한 권한이 없습니다.",
        "instance": "/api/carts/{cartItemId}",
        "timestamp": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```

### 3. Orders

#### 3.1 **GET** `/api/orders`
사용자의 주문목록을 조회합니다.
- **Request Param**
  - userId // **일반적으론 토큰에 저장된 userId를 이용하겠지만 토큰이 없어 대체하였습니다.**

- **[Success]**
    - **Http Code:** `Ok`
    - **Response Body**
    ```json
    [
        {
            "id": "Number",
            "totalPayment": "Number",
            "status": "PAID | SHIPPED | DELIVERED | CANCELED",
            "orderItems": [
                {
                    "name": "String",
                    "price": "Number",
                    "quantity": "Number"
                },
                ...
            ],
            "createdAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        },
        ...
    ]
    ```

#### 3.2 **POST** `/api/orders`
사용자의 장바구니에 담긴 모든 상품을 주문합니다.
- **Request Body**
```json
 {
    "userId" : "Number"
 }
```
- **[Success]**
    - **Http Code:** `Created`
    - **Response Body**
    ```json
    {
            "id": "Number",
            "totalPayment": "Number",
            "status": "PAID",
            "orderItems": [
                {
                    "name": "String",
                    "price": "Number",
                    "quantity": "Number"
                },
                ...
            ],
            "createdAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```
- **[Failed]**
    - **Case:** 외부 결제에 실패한 경우
    - **Http Code:** `Internal Server Error`
    - **Response Body**
    ```json
    {
        "type": "about:blank",
        "title": "Internal Server Error",
        "status": 500,
        "detail": "결제 실패하였습니다. : Something wrong!",
        "instance": "/api/orders",
        "timestamp": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```

#### 3.3 **POST** `/api/orders/{orderId}/cancel`
사용자의 특정 주문을 취소합니다.
- **Request Body**
```json
 {
    "userId" : "Number"
 }
```
- **[Success]**
    - **Http Code:** `Ok`
    - **Response Body**
    ```json
    {
            "id": "Number",
            "totalPayment": "Number",
            "status": "CANCELED",
            "orderItems": [
                {
                    "name": "String",
                    "price": "Number",
                    "quantity": "Number"
                },
                ...
            ],
            "createdAt": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```