@LoginTest
Feature: Login Action

   Scenario: Successful Login with Valid Credentials
      Given User is on Home Page
      When User Navigate to Login Page
      And User enters Credentials to Login
         | username  | password              |
         | tuyucheng | tuyucheng2000@163.com |
      Then Message displayed Login Successfully

   Scenario: Successful Logout
      When User Logout from the Application
      Then Message displayed Logout Successfully