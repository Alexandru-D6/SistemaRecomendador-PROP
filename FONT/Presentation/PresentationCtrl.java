package Presentation;

import java.util.ArrayList;

import Presentation.CustomComponents.RatingsPanel;

public class PresentationCtrl {
    
    private static PresentationCtrl presentationCtrl = null;
    private VistaLogin vistaLogin = null;
    private VistaConfiguraModel vistaConfiguraModel = null;
    private VistaMainMenu vistaMainMenu = null;
    private VistaInformacio vistaInformacio = null;
    private VistaAfegirValoracio vistaAfegirValoracio = null;
    private VistaAfegirItem vistaAfegirItem = null;
    private VistaCreateDerived vistaCreateDerived = null;
    private VistaRecommendations vistaRecommendations = null;
    private VistaRatings vistaRatings = null;

    ArrayList<Object> vistas = new ArrayList<Object>();

    public enum userType {
        ADMIN,
        USER
    }
    public userType currentType = null;

    private PresentationCtrl() {
    }

    public static PresentationCtrl getInstance() {
        if (presentationCtrl == null) presentationCtrl = new PresentationCtrl();
        return presentationCtrl;
    }

    private void closeVistas() {
        if (vistaLogin != null) vistaLogin.makeVisible(false);
        if (vistaConfiguraModel != null) vistaConfiguraModel.makeVisible(false);
        if (vistaMainMenu != null) vistaMainMenu.makeVisible(false);
        if (vistaInformacio != null) vistaInformacio.makeVisible(false);
        if (vistaAfegirValoracio != null) vistaAfegirValoracio.makeVisible(false);
        if (vistaAfegirItem != null) vistaAfegirItem.makeVisible(false);
        if (vistaCreateDerived != null) vistaCreateDerived.makeVisible(false, 0);
        if (vistaRecommendations != null) vistaRecommendations.makeVisible(false);
        if (vistaRatings != null) vistaRatings.makeVisible(false);
    }

    public void initLogin() {
        if (vistaLogin == null) vistaLogin = new VistaLogin(this);
        closeVistas();
        vistaLogin.makeVisible(true);
    }

    public void initConfiguraModel() {
        if (vistaConfiguraModel == null) vistaConfiguraModel = new VistaConfiguraModel(this);
        closeVistas();
        vistaConfiguraModel.makeVisible(true);
    }

    public void initMainMenu() {
        if (vistaMainMenu == null) vistaMainMenu = new VistaMainMenu(this);
        closeVistas();
        vistaMainMenu.makeVisible(true);
    }
    public VistaLogin getLogin() { 
        if (vistaLogin == null) vistaLogin = new VistaLogin(this);
        return vistaLogin;
    }
    public VistaConfiguraModel getConfiguraModel() { 
        if (vistaConfiguraModel == null) vistaConfiguraModel = new VistaConfiguraModel(this);
        return vistaConfiguraModel;
    }
    public VistaMainMenu getMainMenu() { 
        if (vistaMainMenu == null) vistaMainMenu = new VistaMainMenu(this);
        return vistaMainMenu;
    }
    public VistaInformacio getInformacio() { 
        if (vistaInformacio == null) vistaInformacio = new VistaInformacio(this);
        return vistaInformacio;
    }
    public VistaAfegirValoracio getAfegirValoracio(RatingsPanel parent) { 
        if (vistaAfegirValoracio == null) vistaAfegirValoracio = new VistaAfegirValoracio(this);
        vistaAfegirValoracio.setParent(parent);
        return vistaAfegirValoracio; 
    }
    public VistaAfegirItem getAfegirItem() { 
        if (vistaAfegirItem == null) vistaAfegirItem = new VistaAfegirItem(this);
        return vistaAfegirItem;
    }
    public VistaCreateDerived getCreateDerivated() { 
        if (vistaCreateDerived == null) vistaCreateDerived = new VistaCreateDerived(this);
        return vistaCreateDerived; 
    }
    public VistaRecommendations getRecommendations() { 
        if (vistaRecommendations == null) vistaRecommendations = new VistaRecommendations(this);
        return vistaRecommendations; 
    }
    public VistaRatings getRatings() { 
        if (vistaRatings == null) vistaRatings = new VistaRatings(this);
        return vistaRatings;
    }

    public void setCurrentType(userType _type) { currentType = _type; }
    public userType getCurrentType() { return currentType; }

}
