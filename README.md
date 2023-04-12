# ABSA technical interview


### Author : 
Simbarashe Makwangudze
### Email :
smakwangudze@gmail.com
### Cell :
0742769619

>To access application  from  your favourite browser/REST client use the link below
(http://localhost:8082/swagger-ui/#/).
  
## Steps to run the application
***

1. Create a db

>Create a  database  of your   favourite which you will use .In this scenario I used mysql e.g database is absa.
>Add the database credentials in application.properties file found in src/main/resources/application.properties

spring.datasource.username=your_sername
spring.datasource.password=your_password
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/absa
spring.jpa.hibernate.ddl-auto=create

This  will create a db for you, check with your favourite workbench

2. Build and run app

>Build your maven  project  using -- mvn clean install 
> 
>  Change directory to project folder  && java -jar target/AbsaBanking-0.0.1-SNAPSHOT.jar
> 
> Access  the endpoints  as  stated above .I have provided some sample  requests  to test in src/test/resources/scratch.txt

3. Test  Cases
* On customer onboarding 2 accounts are created
* Only Savings Account credited with a joining bonus of R500.00
* Handle internal transactions i.e move money between accounts
* ONly Current account enabled to make payments
* Account cant trandaction is the transaction amount is bigger than balance 
* All payments made into the Savings Account will be credited with a 0.5% interest of
  the current balance
* All payments made from the customer’s account will be charged 0.05% of the transaction amount
* keep track of every transaction performed on the accounts and allow other systems to retrieve these
* send out notifications to the customer for every transaction event.
* Handle transactions on behalf
* Creation of End of  day reconcialiation reports 
* Sending of Single/Multiple transactions on behalf 
* Sending out of Scheduled  EOD reports 

4. Run application as a  docker container
>please read src/test/resources/docker-instructions.txt
