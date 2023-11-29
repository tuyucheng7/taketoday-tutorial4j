@FunctionalTest
Feature: ECommerce Application

   @SmokeTest
   @RegressionTest
   Scenario: Successful Login
      Given This is a blank test

   @RegressionTest
   Scenario: UnSuccessful Login
      Given This is a blank test

   @SmokeTest
   Scenario: Add a product to bag
      Given This is a blank test

   Scenario: Add multiple product to bag
      Given This is a blank test

   @SmokeTest
   @RegressionTest
   Scenario: Remove a product from bag
      Given This is a blank test

   @RegressionTest
   Scenario: Remove all products from bag
      Given This is a blank test

   @SmokeTest
   Scenario: product quantity from bag page
      Given This is a blank test

   Scenario: Decrease product quantity from bag page
      Given This is a blank test

   @SmokeTest
   @End2End
   Scenario: Buy a product with cash payment
      Given This is a blank test

   @SmokeTest
   @End2End
   Scenario: Buy a product with CC payment
      Given This is a blank test

   @End2End
   Scenario Outline: Payment declined
      Given This is a blank test
      Examples:
         | PaymentMethod |
         | CC Card       |
         | DD Card       |
         | Bank Transfer |
         | PayPal        |
         | Cash          |