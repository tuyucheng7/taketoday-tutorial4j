@HookTest
Feature: Test Hooks

   Scenario Outline: This scenario is to test hooks functionality
      Given this is the first step
      When this is the second step
      Then this is the third step

      Examples:
         | Scenario |
         | First    |
         | Second   |