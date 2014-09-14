package br.com.locCar.util;


import java.util.ArrayList;
import java.util.List;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.component.UIXComponentBase;
import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;


public final class GenericTableSelectionHandler {
    private GenericTableSelectionHandler() {
        super();
    }
    
    /**
     * Synchronizes the table UI component row selection with the selection in
     * the ADF binding layer
     * @param selectionEvent event object created by the table component upon row 
     * selection
     */
    public static void makeCurrent(SelectionEvent selectionEvent){
      RichTable _table;
      CollectionModel _tableModel;
      JUCtrlHierBinding _adfTableBinding;
      DCIteratorBinding _tableIteratorBinding;
      Object _selectedRowData;
      JUCtrlHierNodeBinding _nodeBinding;
      Key _rwKey;
      
      _table = (RichTable) selectionEvent.getSource();
      _tableModel = (CollectionModel) _table.getValue();
      _adfTableBinding = (JUCtrlHierBinding) _tableModel.getWrappedData();
      _tableIteratorBinding = _adfTableBinding.getDCIteratorBinding();
      _selectedRowData = _table.getSelectedRowData();
      _nodeBinding = (JUCtrlHierNodeBinding) _selectedRowData;
      _rwKey = _nodeBinding.getRowKey();
      _tableIteratorBinding.setCurrentRowWithKey(_rwKey.toStringFormat(true));        
    } //end method
    
    public static void marcarPrimeiraLinha(final RichTable tabela) {
        marcarPrimeiraLinha(tabela, false);
    } //end method
    
    public static void marcarPrimeiraLinha(final RichTable tabela, final boolean ignorePartialTrigger) {
        if (tabela != null) {
            CollectionModel tableModel;
            JUCtrlHierBinding adfTableBinding;
            DCIteratorBinding iterator;
            Row first;
            List lskey;
            RowKeySetImpl newSelection;
            
            tableModel = (CollectionModel) tabela.getValue();
            adfTableBinding = (JUCtrlHierBinding) tableModel.getWrappedData();
            iterator = adfTableBinding.getDCIteratorBinding();
            
            if (iterator != null && iterator.getAllRowsInRange() != null && iterator.getAllRowsInRange().length > 0) {
                lskey = new ArrayList();
                newSelection = new RowKeySetImpl();
                
                first = iterator.getAllRowsInRange()[0];
                
                lskey.add(first.getKey());
                newSelection.add(lskey);
                
                tabela.setActiveRowKey(lskey);
                tabela.setDisplayRowKey(lskey);
                tabela.setSelectedRowKeys(newSelection);
                
                if (!ignorePartialTrigger) {
                    AdfFacesContext.getCurrentInstance().addPartialTarget(tabela);
                } //end if
            } //end if
        } //end if
    } //end method
    
    public static void marcarUltimaLinha(final RichTable tabela) {
        marcarUltimaLinha(tabela, false);
    } //end method
    
    public static void marcarUltimaLinha(final RichTable tabela, final boolean ignorePartialTrigger) {
        if (tabela != null) {
            CollectionModel tableModel;
            JUCtrlHierBinding adfTableBinding;
            DCIteratorBinding iterator;
            Row last;
            List lskey;
            RowKeySetImpl newSelection;
            
            tableModel = (CollectionModel) tabela.getValue();
            adfTableBinding = (JUCtrlHierBinding) tableModel.getWrappedData();
            iterator = adfTableBinding.getDCIteratorBinding();
            
            if (iterator != null && iterator.getAllRowsInRange() != null && iterator.getAllRowsInRange().length > 0) {
                lskey = new ArrayList();
                newSelection = new RowKeySetImpl();
                
                last = iterator.getRowSetIterator().last();
                
                lskey.add(last.getKey());
                newSelection.add(lskey);
                
                tabela.setActiveRowKey(lskey);
                tabela.setDisplayRowKey(lskey);
                tabela.setSelectedRowKeys(newSelection);
                
                if (!ignorePartialTrigger) {
                    AdfFacesContext.getCurrentInstance().addPartialTarget(tabela);
                } //end if
            } //end if
        } //end if
    } //end method
    
    public static void setFocusOnLine(String iteratorName, RichTable tabela, String attribute2verify, String attributeValue, UIXComponentBase component2focus) {
        DCIteratorBinding iterator;
        RowSetIterator rsi;
        RowKeySet oldSelection;
        iterator = ADFUtils.findIterator(iteratorName);
        rsi = iterator.getRowSetIterator();
        oldSelection = tabela.getSelectedRowKeys();

        if (rsi.first() != null) {
            Row rw = rsi.first();
            while (rsi.hasNext()) {
                if (matchFound(rw, oldSelection, attribute2verify, attributeValue, tabela)) {
                    if (Bean.isNotNull(component2focus)) {
                        JSFUtils.setFocusOnComponent(component2focus);
                    } //end if
                    break;
                } //end if
                rw = rsi.next();
            } //end while
        } //end if
    } //end method

    
    private static boolean matchFound(Row rw, RowKeySet oldSelection, String attribute, String value, RichTable tabela) {
        ArrayList lst;
        RowKeySetImpl newSelection;
        Key key;
        String rowValue;
        boolean found;
        
        lst = new ArrayList(1);
        newSelection = new RowKeySetImpl();
        key = null;
        
        if (Bean.isNull(rw.getAttribute(attribute))) {
            rowValue = null;
        } else {
            rowValue = String.valueOf(rw.getAttribute(attribute));
        } //end if
        
        if (Bean.isNotNull(value) && value.equalsIgnoreCase(rowValue)) {
            found = true;
        } else if (Bean.isNull(value) && Bean.isNull(rowValue)) {
            found = true;
        } else {
            found = false;
        } //end if
        
        if (found) {
            key = rw.getKey();
            lst.add(key);
            tabela.setActiveRowKey(lst);
            newSelection.add(lst);
            makeCurrentNewLine(tabela, newSelection, oldSelection);
        } //end if
        
        return found;

    } //end method

    public static void makeCurrentNewLine(RichTable empTable, RowKeySet newCurrentRow, RowKeySet oldCurrentRow) {
        SelectionEvent selectionEvent;
        selectionEvent = new SelectionEvent(oldCurrentRow, newCurrentRow, empTable);
        selectionEvent.queue();
        AdfFacesContext.getCurrentInstance().addPartialTarget(empTable);
    } //end method

} //end class
