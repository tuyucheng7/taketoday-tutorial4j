Feature: End to End Tests for ToolsQA Book Store API
	Description: The purpose of these test are to cover End to End happy flows for customer.

	Book Store Swagger URL: https://bookstore.toolsqa.com/swagger/

	Background: User generates token for Authorisation
		Given I am an authorized user

	Scenario: Authorized user is able to Add and Remove a book.
		Given A list of books are available
		When I add a book to my reading list
		Then The book is added
		When I remove a book from my reading list
		Then The book is removed