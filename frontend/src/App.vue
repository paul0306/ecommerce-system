<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const productForm = reactive({ productId: '', productName: '', price: '', quantity: '' })
const orderForm = reactive({ memberId: '' })
const orderQueryForm = reactive({ orderId: '' })
const products = ref([])
const quantities = reactive({})
const orderResult = ref(null)
const queriedOrder = ref(null)
const busy = ref(false)
const message = ref('')

const currency = (value) =>
  new Intl.NumberFormat('zh-TW', {
    style: 'currency',
    currency: 'TWD',
    maximumFractionDigits: 0
  }).format(value ?? 0)

const selectedItems = computed(() =>
  products.value
    .filter((product) => Number(quantities[product.productId] || 0) > 0)
    .map((product) => {
      const quantity = Number(quantities[product.productId])
      return { ...product, quantity, subtotal: quantity * Number(product.price) }
    })
)

const totalAmount = computed(() =>
  selectedItems.value.reduce((sum, item) => sum + item.subtotal, 0)
)

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })
  const result = await response.json()
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Request failed')
  }
  return result.data
}

async function loadProducts() {
  products.value = await request('/api/products/available')
  products.value.forEach((product) => {
    if (!(product.productId in quantities)) {
      quantities[product.productId] = 0
    }
  })
}

async function submitProduct() {
  busy.value = true
  message.value = ''
  try {
    await request('/api/products', {
      method: 'POST',
      body: JSON.stringify({
        productId: productForm.productId,
        productName: productForm.productName,
        price: Number(productForm.price),
        quantity: Number(productForm.quantity)
      })
    })
    message.value = '商品新增成功'
    productForm.productId = ''
    productForm.productName = ''
    productForm.price = ''
    productForm.quantity = ''
    await loadProducts()
  } catch (error) {
    message.value = error.message
  } finally {
    busy.value = false
  }
}

async function submitOrder() {
  busy.value = true
  message.value = ''
  try {
    orderResult.value = await request('/api/orders', {
      method: 'POST',
      body: JSON.stringify({
        memberId: orderForm.memberId,
        items: selectedItems.value.map((item) => ({
          productId: item.productId,
          quantity: item.quantity
        }))
      })
    })
    queriedOrder.value = orderResult.value
    orderQueryForm.orderId = orderResult.value.orderId
    message.value = `訂單 ${orderResult.value.orderId} 建立成功`
    Object.keys(quantities).forEach((key) => {
      quantities[key] = 0
    })
    orderForm.memberId = ''
    await loadProducts()
  } catch (error) {
    message.value = error.message
  } finally {
    busy.value = false
  }
}

async function queryOrder() {
  busy.value = true
  message.value = ''
  try {
    queriedOrder.value = await request(`/api/orders/${encodeURIComponent(orderQueryForm.orderId)}`)
    message.value = `訂單 ${queriedOrder.value.orderId} 查詢成功`
  } catch (error) {
    queriedOrder.value = null
    message.value = error.message
  } finally {
    busy.value = false
  }
}

onMounted(loadProducts)
</script>

<template>
  <main class="page">
    <section class="hero">
      <h1>電商購物中心系統</h1>
    </section>

    <p v-if="message" class="message">{{ message }}</p>

    <section class="grid">
      <article class="card">
        <h2>新增商品</h2>
        <form class="form" @submit.prevent="submitProduct">
          <label>
            商品編號
            <input v-model.trim="productForm.productId" placeholder="例如 P004" required />
          </label>
          <label>
            商品名稱
            <input v-model.trim="productForm.productName" placeholder="請輸入商品名稱" required />
          </label>
          <label>
            售價
            <input v-model="productForm.price" type="number" min="1" required />
          </label>
          <label>
            庫存
            <input v-model="productForm.quantity" type="number" min="0" required />
          </label>
          <button :disabled="busy" type="submit">建立商品</button>
        </form>
      </article>

      <article class="card wide">
        <h2>建立訂單</h2>
        <label class="member-field">
          會員編號
          <input v-model.trim="orderForm.memberId" placeholder="例如 55688" />
        </label>

        <div class="table">
          <div class="row header">
            <span>商品編號</span>
            <span>商品名稱</span>
            <span>售價</span>
            <span>庫存</span>
            <span>購買數量</span>
          </div>
          <div v-for="product in products" :key="product.productId" class="row">
            <span>{{ product.productId }}</span>
            <span>{{ product.productName }}</span>
            <span>{{ currency(product.price) }}</span>
            <span>{{ product.quantity }}</span>
            <span>
              <input v-model="quantities[product.productId]" type="number" min="0" :max="product.quantity" />
            </span>
          </div>
        </div>

        <div class="summary">
          <h3>訂單預覽</h3>
          <p v-if="selectedItems.length === 0">尚未選擇商品。</p>
          <div v-else class="summary-list">
            <div v-for="item in selectedItems" :key="item.productId" class="summary-item">
              <span>{{ item.productName }} x {{ item.quantity }}</span>
              <strong>{{ currency(item.subtotal) }}</strong>
            </div>
          </div>
          <div class="summary-total">
            <span>總金額</span>
            <strong>{{ currency(totalAmount) }}</strong>
          </div>
          <button :disabled="busy || !orderForm.memberId || selectedItems.length === 0" @click="submitOrder">
            建立訂單
          </button>
        </div>
      </article>
    </section>

    <section class="card result">
      <h2>查詢訂單</h2>
      <form class="query-form" @submit.prevent="queryOrder">
        <label>
          訂單編號
          <input v-model.trim="orderQueryForm.orderId" placeholder="例如 Ms20260331232925" required />
        </label>
        <button :disabled="busy || !orderQueryForm.orderId" type="submit">查詢訂單</button>
      </form>

      <div v-if="queriedOrder" class="order-panel">
        <p>訂單編號：{{ queriedOrder.orderId }}</p>
        <p>會員編號：{{ queriedOrder.memberId }}</p>
        <p>付款狀態：{{ queriedOrder.payStatus === 1 ? '已付款' : '未付款' }}</p>
        <p>訂單總金額：{{ currency(queriedOrder.totalPrice) }}</p>
        <div class="summary-list">
          <div v-for="item in queriedOrder.items" :key="`${queriedOrder.orderId}-${item.productId}`" class="summary-item">
            <span>{{ item.productName }} x {{ item.quantity }}</span>
            <strong>{{ currency(item.itemPrice) }}</strong>
          </div>
        </div>
      </div>
      <p v-else class="empty-state">輸入訂單編號後即可查詢訂單內容。</p>
    </section>

    <section v-if="orderResult" class="card result">
      <h2>最新建立訂單</h2>
      <p>訂單編號：{{ orderResult.orderId }}</p>
      <p>會員編號：{{ orderResult.memberId }}</p>
      <p>付款狀態：{{ orderResult.payStatus === 1 ? '已付款' : '未付款' }}</p>
      <p>訂單總金額：{{ currency(orderResult.totalPrice) }}</p>
      <div class="summary-list">
        <div v-for="item in orderResult.items" :key="`${orderResult.orderId}-${item.productId}`" class="summary-item">
          <span>{{ item.productName }} x {{ item.quantity }}</span>
          <strong>{{ currency(item.itemPrice) }}</strong>
        </div>
      </div>
    </section>
  </main>
</template>