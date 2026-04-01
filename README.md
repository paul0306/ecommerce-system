# 電商購物中心系統

## 專案結構

```text
.
├─ backend/
│  ├─ src/main/java/com/esun/ecommerce/
│  │  ├─ controller/
│  │  ├─ service/
│  │  ├─ repository/
│  │  ├─ dto/
│  │  ├─ model/
│  │  ├─ common/
│  │  ├─ config/
│  │  └─ db/
│  └─ src/main/resources/
│     ├─ schema.sql
│     ├─ data.sql
│     └─ stored-procedures.sql
├─ frontend/
├─ DB/
└─ README.md
```

## 部屬方式

### 1. 啟動後端

```powershell
cd backend
mvn spring-boot:run
```

### 2. 啟動前端

```powershell
cd frontend
npm install
npm run dev
```

前端啟動後預設位置：
- Frontend URL: `http://localhost:5173`


## 資料庫說明

預設使用 `H2 file database`，啟動時會自動執行：
- `schema.sql`：建立資料表
- `stored-procedures.sql`：建立 H2 alias stored procedures
- `data.sql`：匯入初始資料

### 資料表
- `product`
- `orders`
- `order_detail`

### 題目要求 DB 腳本位置
- [DB/ddl.sql](./DB/ddl.sql)
- [DB/dml.sql](./DB/dml.sql)
- [DB/stored_procedures.sql](./DB/stored_procedures.sql)

## API 說明

### 1. 查詢可購買商品

`GET /api/products/available`

範例：

```http
GET http://localhost:8080/api/products/available
```

### 2. 新增商品

`POST /api/products`

Request Body：

```json
{
  "productId": "P004",
  "productName": "藍牙耳機",
  "price": 2990,
  "quantity": 10
}
```

### 3. 建立訂單

`POST /api/orders`

Request Body：

```json
{
  "memberId": "55688",
  "items": [
    {
      "productId": "P002",
      "quantity": 2
    },
    {
      "productId": "P003",
      "quantity": 1
    }
  ]
}
```
