# Deploy Secured Microservices on AWS with Spring Boot, Terraform, Kubernetes, Keycloak Oauth2 Authorization Server, Github Actions, Spring Cloud Gateway, External DNS, SSL, Nginx Ingress Controller, Spring Cloud Kubernetes, Open API Documentation and Grafana Observability Stack

**Keycloak Administration Console** is available here: **https://keycloak.greeta.net**

###### **admin user:** admin

###### **admin password:** admin

**Movies Online UI**, secured with **Keycloak Server** is available here: **https://movie.greeta.net**

###### **manager user:** admin

###### **manager password:** admin

###### **regular user:** admin

###### **regular password:** admin

# Microservices Deployment on AWS with Spring Boot, Terraform, Kubernetes, Keycloak Oauth2 Authorization Server, Github Actions, Spring Cloud Gateway, External DNS, SSL, Nginx Ingress Controller, Spring Cloud Kubernetes, Open API Documentation and Grafana Observability Stack

## Step 01 - Setup terraform account on AWS:
#### Skip to Step 02, if you already have working Terraform account with all permissions

#### Setting Up Terraform infrastructure

 - Log in to your AWS account from commmand-line

#### Create terraform.auto.tfvars in your erp-infra repository and provide your own aws_region and ssl_certificate_arn

```
aws_region = "eu-central-1"
environment = "dev"
business_division = "it"
cluster_name = "erp-cluster"
ssl_certificate_arn = "arn:aws:acm:eu-central-1:your-certificate-arn"
```

Run terraform

```
terraform apply --auto-approve
```

If you see error during creation of grafana observability stack, run terraform apply again.
Please, note, that terraform will run command aws eks --region ${var.region} update-kubeconfig --name ${var.cluster_name} after creation of kubernetes cluster, which will delete your current ./kube/config file. Don't forget to create backup, if needed!

#### Setting Up AWS Permissions

The first thing we’ll do is make the ops-account user part of a new group called Ops-Accounts. That way we’ll be able to assign new users to the group if we want them to have the same permissions. Use the following command to create a new group called Ops-Accounts:

```
$ aws iam create-group --group-name Ops-Accounts
```

If this is successful, the AWS CLI will display the group that has been created:

```
{

    "Group": {
        "Path": "/",
        "GroupName": "Ops-Accounts",
        "GroupId": "AGPA4IGBHKZTGWGQWW67X",
        "Arn": "arn:aws:iam::842218941332:group/Ops-Accounts",
        "CreateDate": "2020-07-06T15:29:14+00:00"
    }
}
```

Now, we just need to add our user to the new group. Use the following command to do that:

```
$ aws iam add-user-to-group --user-name ops-account --group-name Ops-Accounts
````

If it works, you’ll get no response from the CLI.

Next, we need to attach a set of permissions to our Ops-Account group. 

```
$ aws iam attach-group-policy --group-name Ops-Accounts\
 --policy-arn arn:aws:iam::aws:policy/IAMFullAccess &&\
aws iam attach-group-policy --group-name Ops-Accounts\
 --policy-arn arn:aws:iam::aws:policy/AmazonEC2FullAccess &&\
aws iam attach-group-policy --group-name Ops-Accounts\
 --policy-arn arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess &&\
aws iam attach-group-policy --group-name Ops-Accounts\
 --policy-arn arn:aws:iam::aws:policy/AmazonVPCFullAccess &&\
aws iam attach-group-policy --group-name Ops-Accounts\
 --policy-arn arn:aws:iam::aws:policy/AmazonRoute53FullAccess &&\
aws iam attach-group-policy --group-name Ops-Accounts\
 --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess

```

#### Creating an S3 Backend for Terraform

- If you are hosting your bucket in the us-east-1 region, use the following command:

```
$ aws s3api create-bucket --bucket {YOUR_S3_BUCKET_NAME} --region us-east-1
```

- If you are hosting the s3 bucket in a region other than us-east-1, use the following command:

```
$ aws s3api create-bucket --bucket {YOUR_S3_BUCKET_NAME} \
> --region {YOUR_AWS_REGION} --create-bucket-configuration \
> LocationConstraint={YOUR_AWS_REGION}
```




## Step-02: Setup your local Terraform Environment:

- create private and public SSH Keys. Terraform will use them to run scripts on your EC2 instances:

```
    ssh-keygen -t rsa
````

- go to "**terraform**" folder of this github repository

- create file "**terraform.auto.tfvars**" in "**terraform**" folder:

```
access_ip = "0.0.0.0/0"
public_key_path = "/Users/dddd/.ssh/keymtc.pub"
private_key_path = "/Users/dddd/.ssh/keymtc"
certificate_arn = "arn:aws:acm:ddddddddddddddddddddf"
shared_credentials_file="/Users/ddddd/.aws/credentials"
profile_account="ops-account"
```

- make sure you provide correct path for "**public_key_path**" and "**private_key_path**"

- make sure you provide correct "**certifcate_arn**" for your AWS certificate, registered to your domain. You need to register your domain and create certificate for your domain in AWS

- make sure you provide correct path for your AWS credentials ("**shared_credentials_file**" variable)

- make sure you provide correct aws profile account name for "**profile_account**" variable (it should be "**ops-account**", if you followed instructions in "**Step 01**")

- replace "**skyglass-terraform**" in "**backends.tf**" with the name of your S3 bucket, created in "**Step 01**"

- replace "**us-east-1**" in "**backends.tf**" with the name of your **AWS region**

- replace "**us-east-1**" in "**variables.tf**" with the name of your **AWS region**


- run the following commands:

```
terraform init

terraform validate

terraform apply --auto-approve

``` 

- terraform will automatically create KUBECONFIG file, so you can switch to your created K3S Kubernetes cluster by using
```
export KUBECONFIG=./ks3/k3s.yaml
``` 





## Step-03: Create "customer-management:1.0.0" Docker Image and push it to Docker Hub:

- go back to the root directory of this github repository

- Edit "**pom.xml:**" replace "**skyglass**" in "**<repository>skyglass/${project.name}</repository>**" with the name of your docker hub repository

- Edit "**application.properties:**" replace the value of "**keycloak.auth-server-url**" with the **URL** of your **Keycloak Server**

-  for example, if "**test**" is the name of your docker hub repository, then run: 
```
mvn clean install
docker push test/customer-management:1.0.0
````

## Step-04: Register your domain "test.com", create AWS Certificate for "*.test.com" and create Hosted Zone CNAME Record with DNS Name of your Load Balancer:

- go to "**EC2 -> Load Balancers**" in your AWS Console

- copy DNS name of your load balancer

- go to "**Route53 -> Hosted Zones -> Your Hosted Zone -> Create Record**"

- let's assume that the name of your domain is "**test.com**" and "**DNS name**" of your LoadBalancer is "**mtc-loadbalancer.com**"

- create "**CNAME**" record with the name "**users.test.com**" and the value "**mtc-loadbalancer.com**"

- let's assume that the name of your "**CNAME**" record is "**users.test.com**" 

- let's assume that "**DNS name**" of your Load Balancer is "**mtc-loadbalancer.com**"

- let's assume that you correctly registered your domain, created hosted zone, registered AWS SSL Certificate for your domain "***.test.com**" and created "**CNAME**" record with the name "**users.test.com**" and the value "**mtc-loadbalancer.com**"


## Step-05: Deploy "customer-management" Microservice to AWS:

- go to "**k3s**" folder of this github repository

- Edit "**150-keycloak-config.yaml**": replace "**KEYCLOAK_HOSTNAME**" with the **hostname** of your **Keycloak Server**

- Edit "**250-customermgmt.yaml**": replace "**skyglass/customer-management:1.0.0**" with the name of your docker image

- Edit "**300-traefik-ingress.yaml**": replace "**users.skycomposer.net**" with the name of your sub-domain ("**users.test.com**", for example)

- go back to "**terraform**" directory and run the following commands:
``` 
export KUBECONFIG=./ks3/k3s.yaml

kubectl apply -f ../k3s
``` 

## Step-06: Configure your Keycloak Authorization Server:

- go to "**https://users.test.com/**"

- you will be redirected to **Keycloak Home Page**

- go to "**Administration Console**" and login with admin credentials:

###### **admin user:** admin@keycloak

###### **admin password:** my-keycloak-password

- configure your **Keycloak Server** as described in this article: **https://www.baeldung.com/spring-boot-keycloak**

###### Make sure that you set correct **Valid Redirection URIs**, like this: "**https://users.test.com/customermgmt/***"

###### Make sure that new users have "**user**" role, otherwise you won't be able to see the **customers page**


## Step-07: Test your Microservices:

- go to "**https://users.test.com/customermgmt"

- you should see successfully loaded "**Customer Portal**" page
- Click "**Enter the intranet:**" link: you will be redirected to **Keycloak Login Page**
- Enter correct login and password: you will be redirected to **Existing Customers Page**
- Click "**logout**" link: your user will be successfully logged out

- go to "**https://users.test.com/usermgmt/swagger-ui/index.html**"

- you should see successfully loaded **user-management** "**Swagger UI**" page (deprecated, not protected by Keycloak)

- go to "**https://users.test.com/whoami**"

- you should see successfully loaded "**WhoAmI**" page




### Congratulations! You sucessfully created Minimal Kubernetes Cluster on AWS with Terraform and K3S!
- ### Now you can deploy your own docker containers to this cluster with minimal costs from AWS!
- ### You significantly reduced your AWS bills by removing AWS EKS and NAT gateway!
- #### You also implemented Traefik Ingress Controller, which acts as a Gateway API for your microservices
- #### Now you can add any number of microservices to your K3S Kubernetes Cluster and use only one Gateway Traefik Controller for all these microservices 

- ### You successfully deployed Keycloak Authorization Server, which protects your Spring Boot "Customer Management" Application
- ### Spring Boot seamlessly handled the entire process of calling the Keycloak OAuth2 Authorization Server to authenticate the user
- #### Now you can protect any number of microservices by your Keycloak Server and use Single Sign-On Authentication for all these microservices


## Step-08: Clean-Up:

```
terraform destroy --auto-approve  
```
