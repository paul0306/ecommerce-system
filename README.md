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
cd "C:\program design\ecommerce-yusan\backend"
mvn spring-boot:run
```

### 2. 啟動前端

```powershell
cd "C:\program design\ecommerce-yusan\frontend"
npm install
npm run dev
```

前端啟動後預設位置：
- Frontend URL: `http://localhost:5173`

## 建置方式

### 後端建置

```powershell
cd "C:\program design\ecommerce-yusan\backend"
mvn clean package
```

建置完成後產物位置：
- `backend/target/ecommerce-backend-0.0.1-SNAPSHOT.jar`

可直接執行：

```powershell
cd "C:\program design\ecommerce-yusan\backend"
java -jar target\ecommerce-backend-0.0.1-SNAPSHOT.jar
```

### 前端建置

```powershell
cd "C:\program design\ecommerce-yusan\frontend"
npm install
npm run build
```

建置完成後產物位置：
- `frontend/dist/`

## 資料庫說明

本專案預設使用 `H2 file database` 作為本機示範資料庫，啟動時會自動執行：
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

## 系統設計說明

### 架構
- `controller`：接收 HTTP Request 並回傳 API Response
- `service`：處理商業邏輯，例如建立訂單、計算總金額、驗證庫存
- `repository`：處理資料存取
- `dto`：定義 API 請求與回應格式
- `model`：定義領域模型

### 安全處理
- 使用參數化查詢，避免 SQL Injection
- 對輸入欄位做 Validation 與格式限制
- 對商品名稱等欄位限制特殊字元，降低 XSS 風險
- 建立訂單流程使用 `@Transactional`，避免跨表更新失敗造成資料不一致

## 部署方式

### 後端部署

可以將後端包成 jar 後部署到任何支援 Java 17 的主機，例如：
- Windows Server
- Linux VM
- Docker 容器
- 雲端平台 VM / App Service

部署流程範例：

```powershell
cd backend
mvn clean package
java -jar target\ecommerce-backend-0.0.1-SNAPSHOT.jar
```

### 前端部署

前端建置後的 `frontend/dist/` 可部署到：
- Nginx
- Apache
- GitHub Pages
- Netlify
- Vercel

若部署到正式主機，請將前端 API 目標改為正式後端網址。

## GitHub 建議內容

推送到 GitHub 前，建議至少包含：
- 完整原始碼
- `README.md`
- `DB/` 腳本
- `.gitignore`

建議不要推送：
- `frontend/node_modules/`
- `frontend/dist/`
- `backend/target/`
- `backend/data/`
