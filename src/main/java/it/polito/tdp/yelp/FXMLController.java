/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	Business partenza = cmbLocale.getValue();
    	if(partenza == null) {
    		txtResult.appendText("ERRORE: Selezionare un locale dal menu a tendina!\n");
    	}
    	Double soglia = 0.0;
    	try {
    		soglia = Double.parseDouble(txtX.getText());
    	}catch(NumberFormatException e) {
    		txtResult.appendText("ERRORE: Inserire un valore di soglia compreso tra 0 e 1!\n");
    		return;
    	}
    	if(soglia < 0 || soglia > 1) {
    		txtResult.appendText("ERRORE: Inserire un valore di soglia compreso tra 0 e 1!\n");
    		return;
    	}
    	Business arrivo = this.model.getMigliore();
    	List<Business> percorsoMigliore = new ArrayList<>(this.model.trovaPercorso(soglia, partenza, arrivo));
    	txtResult.appendText("Percorso migliore:\n");
    	
    	for(Business b: percorsoMigliore) {
    		txtResult.appendText(b.getBusinessName()+"\n");
    		   }
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
        txtResult.clear();
        String city = cmbCitta.getValue();
        if(city == null) {
        	txtResult.appendText("ERRORE: Selezionare una citta dal menu a tendina!\n");
        }
        Integer anno = cmbAnno.getValue();
        if(anno == null) {
        	txtResult.appendText("ERRORE: Selezionare un anno dal menu a tendina!\n");
        }
        this.model.creaGrafo(anno, city);
        btnLocaleMigliore.setDisable(false);
        cmbLocale.getItems().addAll(this.model.getVertici());
    	txtResult.appendText("Grafo creato!\n");
    	txtResult.appendText("#VERTICI: "+ this.model.nVertici()+"\n");
    	txtResult.appendText("#ARCHI: "+ this.model.nArchi());
		
    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {

    	txtResult.clear();
    	Business migliore = this.model.getMigliore();
    	btnPercorso.setDisable(false);
    	txtResult.appendText("LOCALE MIGLIORE: " + migliore + "\n");
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    
        for(int year = 2005; year<=2013; year++) {
        	cmbAnno.getItems().add(year);
        }
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	cmbCitta.getItems().addAll(this.model.getAllCities());
    	btnLocaleMigliore.setDisable(true);
    	btnPercorso.setDisable(true);
    	
    
    }
}
