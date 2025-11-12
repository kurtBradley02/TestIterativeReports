package com.poc.models;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class SignUpPage {
    public Page page;

    // Public fields (uninitialized until bind)
    public Locator linkSignupLogin;
    public Locator inputName;
    public Locator inputEmail;
    public Locator buttonSignup;
    public Locator radioMr;
    public Locator inputPassword;
    public Locator dropdownDays;
    public Locator dropdownMonths;
    public Locator dropdownYears;
    public Locator checkboxNewsletter;
    public Locator checkboxOffers;
    public Locator inputFirstName;
    public Locator inputLastName;
    public Locator inputCompany;
    public Locator inputAddress1;
    public Locator inputAddress2;
    public Locator dropdownCountry;
    public Locator inputState;
    public Locator inputCity;
    public Locator inputZipcode;
    public Locator inputMobile;
    public Locator buttonCreateAccount;
    public Locator linkContinue;
    public Locator linkDeleteAccount;

    /** Call this once right after new SignUpPage() */
    public SignUpPage bind(Page page) {
        this.page = page;

        // Use version-safe selectors (works on older Playwright Java)
        linkSignupLogin   = page.locator("a:has-text('Signup / Login')");
        inputName         = page.locator("input[name='name']");
        inputEmail        = page.locator("form:has-text('Signup') >> input[placeholder='Email Address']");
        buttonSignup      = page.locator("button:has-text('Signup')");
        radioMr           = page.locator("input[type='radio'][value='Mr']");
        inputPassword     = page.locator("input[name='password']");
        dropdownDays      = page.locator("#days");
        dropdownMonths    = page.locator("#months");
        dropdownYears     = page.locator("#years");
        checkboxNewsletter= page.locator("input[name='newsletter']");
        checkboxOffers    = page.locator("input[name='optin']");
        inputFirstName    = page.locator("input[name='first_name']");
        inputLastName     = page.locator("input[name='last_name']");
        inputCompany      = page.locator("input[name='company']");
        inputAddress1     = page.locator("input[name='address1']");
        inputAddress2     = page.locator("input[name='address2']");
        dropdownCountry   = page.locator("select[name='country']");
        inputState        = page.locator("input[name='state']");
        inputCity         = page.locator("input[name='city']");
        inputZipcode      = page.locator("#zipcode");
        inputMobile       = page.locator("input[name='mobile_number']");
        buttonCreateAccount = page.locator("button:has-text('Create Account')");
        linkContinue      = page.locator("a:has-text('Continue')");
        linkDeleteAccount = page.locator("a:has-text('Delete Account')");
        return this;
    }
}
