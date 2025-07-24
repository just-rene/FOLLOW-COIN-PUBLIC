<template>
  <v-app>
    <v-main>

      <v-row justify="center" dense>
        <v-col v-for="coin in coins" cols="12" md="6">
          <v-card :color="coin.bgCol" append-icon="" class="mx-auto" prepend-icon="" subtitle="" :title="coin.symbol"
            :append-avatar="coin.symbol.toLowerCase() + '.png'">
            <v-card-text>

            </v-card-text>

            <v-row justify="center">
              <v-expansion-panels>
                <v-expansion-panel title="  " text="">
                  <v-expansion-panel-text>
                    <v-row justify="center">

                      <v-col cols="4">
                        <v-number-input v-model="coin.priceAlarmNegative" max="0" min="-10000" :precision="5" reverse
                          controlVariant="stacked" label="-" :hideInput="false" inset
                          variant="solo-filled"></v-number-input>
                      </v-col>
                      <v-col cols="4">
                        <v-number-input v-model="coin.priceAlarmPositiv" max="10000.00000" min="0" :precision="5"
                          reverse controlVariant="stacked" label="+" :hideInput="false" inset
                          variant="solo-filled"></v-number-input>
                      </v-col>
                    </v-row>
                  </v-expansion-panel-text>

                  <v-col cols="12">
                    <v-card>
                      <div :id="coin.symbol">
                        <apexchart type="bar" height="350" :options="coin.chartData.chartOptions"
                          :series="coin.chartData.series">
                        </apexchart>
                      </div>
                    </v-card>
                  </v-col>
                </v-expansion-panel>
              </v-expansion-panels>


            </v-row>
          </v-card>
        </v-col>

      </v-row>
    </v-main>
  </v-app>
</template>

<script>
export default {
  data: function () {
    return {
      host: process.env.VUE_APP_BASE_URL,
      coins: [],
      tickerSubs: 0,
    }
  },
  mounted: function () {
    let coinSymbols = ["BTC", "ETH", "ADA", "DOGE"];
    for (let index = 0; index < coinSymbols.length; index++) {
      this.fetchHistory(coinSymbols[index]);
    }
    this.getTickerSubs();
  },

  methods: {



    async fetchHistory(coinSymbols) {

      let chartSkelleton = this.createNewChart();
      let coin = new Coin(coinSymbols, chartSkelleton);

      let res = await fetch("http://localhost:8082/history/" + coin.symbol);
      let data = await res.json();

      this.addDataToChart(data, coin);
    },

    async addDataToChart(data, coin) {
      for (let index = 0; index < data.length; index++) {
        coin.chartData.series[0].data.push(data[index].differenceAbsolute);
        coin.chartData.chartOptions.xaxis.categories.push(data[index]["_id"]["startDateTime"]);

        coin.uuids.add(data[index]["uuid"])
      }

      this.coins.push(coin);
    },

    async getTickerSubs() {
      const evtSource = new EventSource("http://localhost:8082" + "/ticker-stream");
      evtSource.onmessage = (event) => {

        let eventData = JSON.parse(event.data);
        let symbol = JSON.parse(event.data)["_id"].symbol;

        for (let index = 0; index < this.coins.length; index++) {
          console.log(this.coins[index]);
          if (this.coins[index].symbol == symbol) {


            this.checkPriceAlarm(eventData["differenceAbsolute"], this.coins[index]);

            this.coins[index].chartData.chartOptions.xaxis.categories.push(eventData["_id"]["startDateTime"]);
            this.coins[index].chartData.series[0].data.push(eventData["differenceAbsolute"]);
          }
        }

      };

    },
    checkPriceAlarm(priceDiff, coin) {

      if (priceDiff < coin.priceAlarmNegative && coin.priceAlarmNegative != 0) {
        coin.priceAlarmNegativeActive = true;
        coin.bgCol = "red";
      }

      if (priceDiff > coin.priceAlarmPositiv && coin.priceAlarmPositiv != 0) {
        coin.priceAlarmPositivActive = true;
        coin.bgCol = "green";
      }


    },

    createNewChart() {
      return {
        series: [{
          name: 'Price changes',
          data: [

          ]
        }],
        chartOptions: {
          chart: {
            type: 'bar',
          },
          plotOptions: {
            bar: {
              colors: {
                ranges: [{
                  from: 0,
                  to: 100000000,
                  color: 'green'
                }, {
                  from: -100000000,
                  to: 0,
                  color: 'red'
                }]
              },
              columnWidth: '80%',
            }
          },
          dataLabels: {
            enabled: false,
          },
          yaxis: {
            title: {
              text: 'change',
            },
            labels: {
              formatter: function (y) {
                return Number(y).toFixed(5); //.toFixed(0) + "%";
              }
            }
          },
          xaxis: {
            type: 'datetime',
            categories: [

            ],
            labels: {
              datetimeFormatter: {
                minute: 'HH:mm:ss',
              },
              rotate: -90,
            }
          }
        },
      }
    }

  },
  computed: {

  },

}



class Coin {
  constructor(symbol, chartData) {
    this.symbol = symbol;
    this.chartData = chartData;
    this.uuids = new Set();

    this.priceAlarmPositivOn = true;
    this.priceAlarmPositiv = 0.0;
    this.priceAlarmPositivActive = false;

    this.priceAlarmNegativeOn = true;
    this.priceAlarmNegative = -0.0;
    this.priceAlarmNegativeActive = false;

    this.bgCol = "white";

  }
}

</script>
