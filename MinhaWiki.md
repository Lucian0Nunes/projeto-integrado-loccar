/**Antes da página ou popup ser renderizado, você deve executar este método passando como parâmetro o iterator que deseja exibir na tela.**/

> private void limparIterator(DCIteratorBinding iterator) {
> > iterator.executeQuery();
> > iterator.setRefreshOption(0);
> > iterator.refresh(DCIteratorBinding.RANGESIZE\_UNLIMITED);

> }