package cn.tuyucheng.taketoday.cucumber.e2e.managers;

import cn.tuyucheng.taketoday.cucumber.e2e.pages.*;
import org.openqa.selenium.WebDriver;

public class PageObjectManager {

   private final WebDriver driver;
   private ProductListPage productListPage;
   private CartPage cartPage;
   private HomePage homePage;
   private CheckoutPage checkoutPage;
   private ConfirmationPage confirmationPage;

   public PageObjectManager(WebDriver driver) {
      this.driver = driver;
   }

   public HomePage getHomePage() {
      return homePage == null ? homePage = new HomePage(driver) : homePage;
   }

   public ProductListPage getProductListPage() {
      return productListPage == null ? productListPage = new ProductListPage(driver) : productListPage;
   }

   public CartPage getCartPage() {
      return cartPage == null ? cartPage = new CartPage(driver) : cartPage;
   }

   public CheckoutPage getCheckoutPage() {
      return checkoutPage == null ? checkoutPage = new CheckoutPage(driver) : checkoutPage;
   }

   public ConfirmationPage getConfirmationPage() {
      return confirmationPage == null ? confirmationPage = new ConfirmationPage(driver) : confirmationPage;
   }
}