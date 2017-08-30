import { browser, element, by } from 'protractor/built';

export class DatasetPage {

  private url;

  constructor(url){
    this.url = url;
  }

  navigateTo() {
    return browser.get(this.url);
  }

  getParagraphText() {
    return element(by.css('similar-molecule div h4')).getText();
  }

  getNthSimilar(n){
    return element(by.css('similar-molecule div ul.nobullet li:nth-child('+n+') a')).getText();
  }

  getSimilarCount(){
    return element(by.css('similar-molecule div ul.nobullet')).all(by.tagName('li')).count();
  }

  getOmicsImageTitle(){
    return element(by.css('app-omics-image img')).getTitle();
  }

}
