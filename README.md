# FOLLOW COIN 
> Follow-Coin is an fully **Event-Driven Microservice** project, that helps you track your **crypto portfolio changes**. \
> It is designed to run in **AWS Fargate** with **SQS**.




## Overview 
<img src="pics/showcase.PNG" alt="drawing" style="border-radius:0.7%;" width="800"/>

## Set your negative / positive limits
<img src="pics/s3.PNG" alt="drawing" style="border-radius:0.7%;" width="800"/>

## Get a visual hint if the limit is crossed
<img src="pics/new.PNG" alt="drawing" style="border-radius:0.7%;" width="800"/>

## More Features
1. **Fail-Safe** thanks to **microservice architecture** (same services exists multiple times) <br/>
2. **Event-Driven** architecture through **reactive programming** and **AWS SQS** (at least once  delivery) <img src="pics/download.png" alt="drawing" width="20"/> <br/>
3. Filters duplicates automatically <br/>
4. Uses **Coinbase-API** for reliable data <img src="pics/coinbase-logo.png" alt="drawing" width="20"/> <br/>

## CDK and Github Actions
I added a Github Actions scripts for deployment to AWS ECR. [Github Actions link](https://github.com/just-rene/follow-coin-public/tree/main/.github/workflows) <br />
A proper CDK script will be in the next version.


