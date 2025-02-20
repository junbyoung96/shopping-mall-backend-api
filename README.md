## Shopping mall API

## 개발하며 신경썼던 부분들
- **트랜잭션 처리**
>
주문 생성 프로세스에 트랜잭션을 적용해, 주문이 실패할 경우 주문 이력이 저장되지 않고, 변경된 상품 재고가 자동으로 롤백되도록 구현했습니다.  
해당 트랜잭션의 고립수준을 **READ_COMMITTED**으로 설정하여 데이터 무결성과 동시성 성능을 향상시켰습니다.
- **외부 결제 API**
>
외부 결제 API를 호출하기 위해 **WebClient**를 도입했습니다.  
현재는 동기적으로 결제 요청을 처리하고 있지만, 향후 비동기 처리나 대규모 트래픽을 안정적으로 처리할 수 있도록 확장 가능성을 염두에 두고 WebClient를 선택했습니다.
- **효율성**
>
엔티티마다 하위 엔티티와의 연관이 많아 **N+1 문제**가 발생하기 쉬운 구조였습니다.  
이를 해결하기 위해 **지연 로딩**과 **@EntityGraph**를 활용해 N+1 문제를 최소화하고 효율성을 높였습니다.  
CartItem은 Cart와, Order는 User와 강하게 결합되어 있어, 해당 엔티티에 **인덱스**를 생성해 조회 성능을 높였습니다.
>
**엔티티 관계도**
| From       | To         | Relationship |
|------------|-----------|--------------|
| **User**   | **Cart**   | 1:1          |
| **Cart**   | **CartItem**  | 1:N       |
| **CartItem** | **Product**  | 1:1      |
| **User**   | **Order**  | 1:N          |
| **Order**  | **OrderItem** | 1:N       |
| **OrderItem** | **Product**  | 1:1     |
>

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
동일한 상품이 장바구니에 존재하는 상태에서 상품을 추가하는 경우, 기존 등록된 수량에 추가되도록 구현하였습니다.
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

#### 2.2 **PATCH** `api/carts/{cartItemId}`
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
- **[Failed]**
    - **Case:** 주문 번호가 유효하지 않은 경우
    - **Http Code:** `Not Found`
    - **Response Body**
    ```json
    {
        "type": "about:blank",
        "title": "Not Found",
        "status": 404,
        "detail": "Order with ID {orderId} not found",
        "instance": "/api/orders/{orderId}/cancel",
        "timestamp": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
    ```


## 테이블 구조

#### users
| 컬럼명 | 데이터타입 | 설명 | 제약조건 |
|-------|-------|-------|-------|
| id | bigInt | 사용자 ID | Primary Key |
| name | varchar(50) | 사용자 이름 | NOT NULL |
| created_at | datetime | 사용자 등록시간 | NOT NULL |
| updated_at | datetime | 사용자 변경시간 | - |

#### products
| 컬럼명 | 데이터타입 | 설명 | 제약조건 |
|-------|-------|-------|-------|
| id | bigInt | 상품 ID | Primary Key |
| name | varchar(255) | 상품 이름 | NOT NULL |
| description | varchar(1000) | 상품 설명 | - |
| price | bigInt | 상품 가격 | NOT NULL, price >= 0 |
| stock | int | 상품 재고 | NOT NULL, stock >= 0 |
| created_at | datetime | 상품 등록시간 | NOT NULL |
| updated_at | datetime | 상품 변경시간 | - |

#### carts
| 컬럼명 | 데이터타입 | 설명 | 제약조건 |
|-------|-------|-------|-------|
| id | bigInt | 장바구니 ID | Primary Key |
| user_id | bigInt | 사용자 ID | Foreign Key, NOT NULL |

#### cart_items
| 컬럼명 | 데이터타입 | 설명 | 제약조건 |
|-------|-------|-------|-------|
| id | bigInt | 장바구니 항목 ID | Primary Key |
| cart_id | bigInt | 장바구니 ID | Foreign Key, NOT NULL |
| product_id | bigInt | 상품 ID | Foreign Key, NOT NULL |
| quantity | int | 상품 가격 | NOT NULL, quantity >= 0 |
| created_at | datetime | 카트 상품 등록시간 | NOT NULL |
| updated_at | datetime | 카트 상품 변경시간 | - |

#### orders
| 컬럼명 | 데이터타입 | 설명 | 제약조건 |
|-------|-------|-------|-------|
| id | bigInt | 주문 ID | Primary Key |
| user_id | bigInt | 사용자 ID | Foreign Key, NOT NULL |
| total_payment | bigInt | 주문 총액 | NOT NULL |
| status | enum('PROCESSING','PAID','SHIPPED','DELIVERED','CANCELED') | 주문 상태 | NOT NULL |
| created_at | datetime | 주문 등록시간 | NOT NULL |
| updated_at | datetime | 주문 변경시간 | - |

#### order_items
| 컬럼명 | 데이터타입 | 설명 | 제약조건 |
|-------|-------|-------|-------|
| id | bigInt | 주문 상품 ID | Primary Key |
| order_id | bigInt | 주문 ID | Foreign Key, NOT NULL |
| product_id | bigInt | 상품 ID | Foreign Key, NOT NULL |
| quantity | int | 주문 상품 개수 | NOT NULL, quantity > 0 |

#### payment_log
| 컬럼명 | 데이터타입 | 설명 | 제약조건 |
|-------|-------|-------|-------|
| id | bigInt | log ID | Primary Key |
| user_id | bigInt | 사용자 ID | NOT NULL |
| order_id | bigInt | 주문 ID | NOT NULL |
| total_payment | bigInt | 주문 총액 | NOT NULL |
| transaction_id | varchar(255) | 트랜잭션 ID | - |
| message | varchar(255) | API 응답메시지 | - |
| status | enum('FAILED','SUCCESS') | 결제 여부 | NOT NULL |
| created_at | datetime | 주문 등록시간 | NOT NULL |
