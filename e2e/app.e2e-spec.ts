import { DatasetPage } from './app.po';

describe('OmicsDI app', function() {
  let page: DatasetPage;

  beforeEach(() => {
    page = new DatasetPage('http://www.omicsdi.org/dataset/atlas-experiments/E-GEOD-30999');
  });

  it('should display correct similarity', () => {
    page.navigateTo();

    expect<any>(page.getParagraphText()).toEqual('Only show the datasets with similarity scores above: 0.5');

    expect<any>(page.getNthSimilar(1)).toContain('Gene expression profiling in psoriatic');

    expect<any>(page.getNthSimilar(2)).toContain('Re-analysis by microarray using cDNA');

    expect<any>(page.getSimilarCount()).toEqual(5);
  });
});



