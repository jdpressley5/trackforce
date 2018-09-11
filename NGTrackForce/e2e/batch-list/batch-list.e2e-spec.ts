import { BatchList } from './batch-list.po';
import { Navbar } from "../navbar/navbar.po";
import { LoginPage } from '../login/login.po';
import { TestConfig } from "../configuration/test-config";

let navbar          : Navbar;
let page            : LoginPage;
let testConfig      : TestConfig;
let batchlist       : BatchList;
let baseURL         : string;

xdescribe('the bactch-list tab', () => {

  beforeAll(() => {
      navbar = new Navbar();
      page = new LoginPage();
      testConfig = new TestConfig();
      batchlist = new BatchList();
      baseURL = testConfig.getBaseURL();
      page.navigateTo();
      navbar.logIn("TestAdmin","TestAdmin");
      navbar.goToBatchList();
  });

  it('should have a batch list table', () => {
    expect(batchlist.getBatchListTable().isPresent()).toBe(true);
  });

  it('should have a start calander', () => {
    expect(batchlist.getFirstCalander().isPresent()).toBe(true);
  });

  it('should have a end calander', () => {
    expect(batchlist.getSecondCalander().isPresent()).toBe(true);
  });
  it('should have a submit button', () => {
    expect(batchlist.getSubmitBtn().isPresent()).toBe(true);
  });
  it('should have a reset button', () => {
    expect(batchlist.getResetBtn().isPresent()).toBe(true);
  });
  it('should have a pie cart', () => {
    expect(batchlist.getPieCart().isPresent()).toBe(true);
  });
  it('should not have a pie cart after the reset button is clicked', () => {
    batchlist.getResetBtn().click();
    expect(batchlist.getPieChartPlaceHolder().getText()).toContain('No Batch Found');
  });
  afterAll(() => {
    page.getlogoutButton().click();
  });
});

xdescribe('The All Batches table', () => {

  beforeAll(() => {
      navbar = new Navbar();
      page = new LoginPage();
      testConfig = new TestConfig();
      batchlist = new BatchList();
      baseURL = testConfig.getBaseURL();
      page.navigateTo();
      navbar.logIn("TestAdmin","TestAdmin");
      navbar.goToBatchList();
  });

  it('should direct you to a batch page', () => {
    let url1 = batchlist.getCurrentUrl();
    batchlist.getBatch().click();
    expect(batchlist.getCurrentUrl()).not.toEqual(url1);
  });

  it('should direct you to a batch page with a chart ', () => {
    expect(batchlist.getBatchCart().isPresent()).toBe(true);
  });

  it('should direct you to a batch page with a table ', () => {
    expect(batchlist.getBatchTable().isPresent()).toBe(true);
  });

  afterAll(() => {
    page.getlogoutButton().click();
  });
});
