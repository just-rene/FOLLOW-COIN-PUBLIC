import { registerPlugins } from '@/plugins'
import App from './App.vue'
import { createApp } from 'vue'
import VueApexCharts from "vue3-apexcharts";

import 'unfonts.css'

const app = createApp(App)

registerPlugins(app)

app.use(VueApexCharts).mount('#app')
