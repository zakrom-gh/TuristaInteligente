/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turistainteligente.managed;

import com.turistainteligente.facade.PaqueteFacadeLocal;
import com.turistainteligente.facade.TarifaHabitacionFacadeLocal;
import com.turistainteligente.facade.TarifaPaqueteFacadeLocal;
import com.turistainteligente.facade.TipoPaqueteIntFacadeLocal;
import com.turistainteligente.facade.TipoPaqueteNacFacadeLocal;
import com.turistainteligente.model.Paquete;
import com.turistainteligente.model.TarifaHabitacion;
import com.turistainteligente.model.TarifaPaquete;
import com.turistainteligente.model.TipoPaqueteInt;
import com.turistainteligente.model.TipoPaqueteNac;
import com.turistainteligente.model.Usuario;
import com.turistainteligente.qualifiers.LoggedIn;
import com.turistainteligente.util.Util;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 *
 * @author HectorFlechaRoja
 */
@Named(value = "paqueteManagedBean")
@SessionScoped
public class PaqueteManagedBean implements Serializable {

    /**
     * Creates a new instance of PaqueteManagedBean
     */
    public PaqueteManagedBean() {
    }
    @EJB
    private PaqueteFacadeLocal paqueteFacadeLocal;
    @EJB
    private TipoPaqueteIntFacadeLocal tipoPaqueteIntFacadeLocal;
    @EJB
    private TipoPaqueteNacFacadeLocal tipoPaqueteNacFacadeLocal;
    @EJB
    private TarifaHabitacionFacadeLocal tarifaHabitacionFacadeLocal;
    @EJB
    private TarifaPaqueteFacadeLocal tarifaPaqueteFacadeLocal;
    private Paquete paquete;
    private Paquete requestedPaqueteById;
    private String tipoPaquete;
    private String tipoTarifa;
    private char indPaquete;
    private char indTarifa;
    private TarifaHabitacion tarifaHabitacion;
    private TarifaPaquete tarifaPaquete;
    private TipoPaqueteInt tipoPaqueteInt;
    private TipoPaqueteNac tipoPaqueteNac;
    static final Logger log = Logger.getLogger(PaqueteManagedBean.class.getName());
    @Inject
    @LoggedIn
    private Usuario currentUser;
    private int idPaquete;

    public String create() {
        try {
//            se crea primero el tipo de tarifa
            if (getTipoTarifa().equals(Util.TipoTarifa.H.toString())) {
                getTarifaHabitacion().setUsrRegistro(currentUser.getEmail());
                getTarifaHabitacion().setFecRegistro(new Date());
                tarifaHabitacionFacadeLocal.create(getTarifaHabitacion());
            } else {
                getTarifaPaquete().setUsrRegistro(currentUser.getEmail());
                getTarifaPaquete().setFecRegistro(new Date());
                tarifaPaqueteFacadeLocal.create(getTarifaPaquete());
            }
//            se crea el tipo de paquete y se añade el tipo de tarifa
            if (getTipoPaquete().equals(Util.TipoPaquete.I.toString())) {
                if (getTipoTarifa().equals(Util.TipoTarifa.H.toString())) {
                    getTipoPaqueteInt().setTipoTarifa(Util.TipoTarifa.H.toString());
                    getTipoPaqueteInt().setIdTarifaHabitacion(getTarifaHabitacion());
                } else {
                    getTipoPaqueteInt().setTipoTarifa(Util.TipoTarifa.P.toString());
                    getTipoPaqueteInt().setIdTarifaPaquete(getTarifaPaquete());
                }
                getTipoPaqueteInt().setUsrRegistro(currentUser.getEmail());
                getTipoPaqueteInt().setFecRegistro(new Date());
                tipoPaqueteIntFacadeLocal.create(getTipoPaqueteInt());
                paquete.setIdTipoPaqueteInt(getTipoPaqueteInt());
            } else {
                if (getTipoTarifa().equals(Util.TipoTarifa.H.toString())) {
                    getTipoPaqueteNac().setTipoTarifa(Util.TipoTarifa.H.toString());
                    getTipoPaqueteNac().setIdTarifaHabitacion(getTarifaHabitacion());
                } else {
                    getTipoPaqueteNac().setTipoTarifa(Util.TipoTarifa.P.toString());
                    getTipoPaqueteNac().setIdTarifaPaquete(getTarifaPaquete());
                }
                getTipoPaqueteNac().setUsrRegistro(currentUser.getEmail());
                getTipoPaqueteNac().setFecRegistro(new Date());
                tipoPaqueteNacFacadeLocal.create(getTipoPaqueteNac());
                paquete.setIdTipoPaqueteNac(getTipoPaqueteNac());
            }
            paquete.setUsrRegistro(currentUser.getEmail());
            paquete.setFecRegistro(new Date());
            paqueteFacadeLocal.create(paquete);
            Util.addSuccessMessage("Paquete creado con éxito");
            log.log(Level.INFO, "Paquete {0} creado", paquete.getIdPaquete());
            initNewPaquete();
            return "./listado.jsf?faces-redirect=true";
//            return "./listado.jsf";
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error al crear el paquete {0}", e.getMessage());
            Util.addErrorMessage("Error al crear el paquete");
            e.printStackTrace();
            return null;
        }
    }

    public String update() {
        try {
            if (getTipoTarifa().equals(Util.TipoTarifa.H.toString())) {
//                se revisa si se está modificando una tarifa de habitacion o se
//                esta creando una nueva
                if (getTarifaHabitacion().getIdTarifaHabitacion() != null) {
                    getTarifaHabitacion().setUsrModificacion(currentUser.getEmail());
                    getTarifaHabitacion().setFecModificacion(new Date());
                    tarifaHabitacionFacadeLocal.edit(getTarifaHabitacion());
                } else {
                    getTarifaHabitacion().setUsrRegistro(currentUser.getEmail());
                    getTarifaHabitacion().setFecRegistro(new Date());
                    tarifaHabitacionFacadeLocal.create(getTarifaHabitacion());
                    tarifaPaqueteFacadeLocal.remove(getTarifaPaquete());
                }
            } else {
//                se revisa si se está modificando una tarifa de paquete o se
//                esta creando una nueva
                if (getTarifaPaquete().getIdTarifaPaquete() != null) {
                    getTarifaPaquete().setUsrModificacion(currentUser.getEmail());
                    getTarifaPaquete().setFecModificacion(new Date());
                    tarifaPaqueteFacadeLocal.edit(getTarifaPaquete());
                } else {
                    getTarifaPaquete().setUsrRegistro(currentUser.getEmail());
                    getTarifaPaquete().setFecRegistro(new Date());
                    tarifaPaqueteFacadeLocal.create(getTarifaPaquete());
                    tarifaHabitacionFacadeLocal.remove(getTarifaHabitacion());
                }
            }
//            se crea el tipo de paquete y se añade el tipo de tarifa
            if (getTipoPaquete().equals(Util.TipoPaquete.I.toString())) {
                if (getTipoTarifa().equals(Util.TipoTarifa.H.toString())) {
                    getTipoPaqueteInt().setTipoTarifa(Util.TipoTarifa.H.toString());
                    getTipoPaqueteInt().setIdTarifaHabitacion(getTarifaHabitacion());
                } else {
                    getTipoPaqueteInt().setTipoTarifa(Util.TipoTarifa.P.toString());
                    getTipoPaqueteInt().setIdTarifaPaquete(getTarifaPaquete());
                }
                if (getTipoPaqueteInt().getIdTipoPaqueteInt() != null) {
                    getTipoPaqueteInt().setUsrModificacion(currentUser.getEmail());
                    getTipoPaqueteInt().setFecModificacion(new Date());
                    tipoPaqueteIntFacadeLocal.edit(getTipoPaqueteInt());
                } else {
                    getTipoPaqueteInt().setUsrRegistro(currentUser.getEmail());
                    getTipoPaqueteInt().setFecRegistro(new Date());
                    tipoPaqueteIntFacadeLocal.create(getTipoPaqueteInt());
                    tipoPaqueteNacFacadeLocal.remove(getTipoPaqueteNac());
                }
                getRequestedPaqueteById().setIdTipoPaqueteInt(getTipoPaqueteInt());
            } else {
                if (getTipoTarifa().equals(Util.TipoTarifa.H.toString())) {
                    getTipoPaqueteNac().setTipoTarifa(Util.TipoTarifa.H.toString());
                    getTipoPaqueteNac().setIdTarifaHabitacion(getTarifaHabitacion());
                } else {
                    getTipoPaqueteNac().setTipoTarifa(Util.TipoTarifa.P.toString());
                    getTipoPaqueteNac().setIdTarifaPaquete(getTarifaPaquete());
                }
                if (getTipoPaqueteNac().getIdTipoPaqueteNac() != null) {
                    getTipoPaqueteNac().setUsrModificacion(currentUser.getEmail());
                    getTipoPaqueteNac().setFecModificacion(new Date());
                    tipoPaqueteNacFacadeLocal.edit(getTipoPaqueteNac());
                } else {
                    getTipoPaqueteNac().setUsrRegistro(currentUser.getEmail());
                    getTipoPaqueteNac().setFecRegistro(new Date());
                    tipoPaqueteNacFacadeLocal.create(getTipoPaqueteNac());
                    tipoPaqueteIntFacadeLocal.remove(getTipoPaqueteInt());
                }
                getRequestedPaqueteById().setIdTipoPaqueteNac(getTipoPaqueteNac());
            }
            getRequestedPaqueteById().setUsrModificacion(currentUser.getEmail());
            getRequestedPaqueteById().setFecModificacion(new Date());
            paqueteFacadeLocal.edit(getRequestedPaqueteById());
            initNewPaquete();
            log.log(Level.INFO, "Paquete {0} modificado con exito", getRequestedPaqueteById().getIdPaquete());
            Util.addSuccessMessage("Paquete modificado con éxito");
            return "./listado.jsf?faces-redirect=true";
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error al modificar el paquete {0}", e.getMessage());
            Util.addErrorMessage("Error al modificar el paquete");
            return null;
        }
    }

    public String delete() {
        try {
//            if (getRequestedTarifaHabitacionById() != null) {
//                tarifaHabitacionFacadeLocal.remove(getRequestedTarifaHabitacionById());
//            } else if (getRequestedTarifaPaqueteById() != null) {
//                tarifaPaqueteFacadeLocal.remove(getRequestedTarifaPaqueteById());
//            }
//
//            if (getRequestedTipoPaqueteIntById() != null) {
//                tipoPaqueteIntFacadeLocal.remove(getRequestedTipoPaqueteIntById());
//            } else if (getRequestedTipoPaqueteNacById() != null) {
//                tipoPaqueteNacFacadeLocal.remove(getRequestedTipoPaqueteNacById());
//            }
            paqueteFacadeLocal.remove(getRequestedPaqueteById());
            Util.addSuccessMessage("Paquete eliminado con éxito");
            log.log(Level.INFO, "Paquete {0} eliminado", getRequestedPaqueteById().getIdPaquete());
             return "./listado.jsf?faces-redirect=true";
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error al eliminar el paquete{0}", e.getMessage());
            Util.addErrorMessage("Error al eliminar el paquete");
            return null;
        }
    }

    public String redirectModificar() {
        setIdPaquete(Integer.parseInt(Util.getRequestParameter("idPaquete")));
        return "./modificarPaquete.jsf?faces-redirect=true&includeViewParams=true";
    }

    @Named
    @Produces
    public Paquete getRequestedPaqueteById() {
        if (requestedPaqueteById == null) {
            requestedPaqueteById = findPaqueteById();
        }
        return requestedPaqueteById;
    }

    public Paquete findPaqueteById() {
        try {
            return paqueteFacadeLocal.find(getIdPaquete());
        } catch (Exception e) {
            return null;
        }
    }

    @Named
    @Produces
    public List<Paquete> getPaquetesPaged() {
        try {
            return paqueteFacadeLocal.findAll();
        } catch (Exception e) {
            return null;
        }

    }

//    @Named
//    @Produces
//    public List<SelectItem> getPaquetesItemsPaged() {
//        List<SelectItem> items = new ArrayList<SelectItem>();
//        List<Paquete> paquetes = paqueteFacadeLocal.findAll();
//        for (Paquete c : paquetes) {
//            items.add(new SelectItem(c, c.getNombre() + c.getPrimerApellido() + c.getSegundoApellido()));
//        }
//        return items;
//    }
    /**
     * @return the paquete
     */
    @Produces
    @Named
    public Paquete getPaquete() {
        return paquete;
    }

    @PostConstruct
    public void initNewPaquete() {
        paquete = new Paquete();
        setTarifaPaquete(new TarifaPaquete());
        setTipoPaqueteInt(new TipoPaqueteInt());
        setTipoPaqueteNac(new TipoPaqueteNac());
        setTarifaHabitacion(new TarifaHabitacion());
        setTipoPaquete("");
        setTipoTarifa("");
        setIndPaquete(' ');
        setIndTarifa(' ');
        setIdPaquete(0);
    }

    /**
     * @return the tipoPaquete
     */
    public String getTipoPaquete() {
        if (requestedPaqueteById != null) {
            if (requestedPaqueteById.getIdTipoPaqueteInt() != null || requestedPaqueteById.getIdTipoPaqueteNac().getIdTipoPaqueteNac() != null) {
                tipoPaquete = requestedPaqueteById.getIdTipoPaqueteInt() != null ? Util.TipoPaquete.I.toString() : Util.TipoPaquete.N.toString();
            }
        }
        return tipoPaquete;
    }

    /**
     * @param tipoPaquete the tipoPaquete to set
     */
    public void setTipoPaquete(String tipoPaquete) {
        this.tipoPaquete = tipoPaquete;
    }

    /**
     * @return the tipoTarifa
     */
    public String getTipoTarifa() {
        if (requestedPaqueteById != null) {
            if (requestedPaqueteById.getIdTipoPaqueteInt() != null || requestedPaqueteById.getIdTipoPaqueteNac() != null) {
                if (requestedPaqueteById.getIdTipoPaqueteInt() != null) {
                    if (requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaHabitacion() != null
                            || requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaPaquete() != null) {
                        tipoTarifa = requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaHabitacion() != null ? Util.TipoTarifa.H.toString() : Util.TipoTarifa.P.toString();
                    }
                } else if (requestedPaqueteById.getIdTipoPaqueteNac() != null) {
                    if (requestedPaqueteById.getIdTipoPaqueteNac().getIdTarifaHabitacion() != null
                            || requestedPaqueteById.getIdTipoPaqueteNac().getIdTarifaPaquete() != null) {
                        tipoTarifa = requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaHabitacion() != null ? Util.TipoTarifa.H.toString() : Util.TipoTarifa.P.toString();
                    }
                }
            }
        }
        return tipoTarifa;
    }

    /**
     * @param tipoTarifa the tipoTarifa to set
     */
    public void setTipoTarifa(String tipoTarifa) {
        this.tipoTarifa = tipoTarifa;
    }

    /**
     * @return the indPaquete
     */
    public char getIndPaquete() {
        if (getTipoPaquete() != null) {
            if (getTipoPaquete().equals(Util.TipoPaquete.I.toString())) {
                indPaquete = 'I';
            } else if (getTipoPaquete().equals(Util.TipoPaquete.N.toString())) {
                indPaquete = 'N';
            }
        }
        return indPaquete;
    }

    /**
     * @param indPaquete the indPaquete to set
     */
    public void setIndPaquete(char indPaquete) {
        this.indPaquete = indPaquete;
    }

    /**
     * @return the indTarifa
     */
    public char getIndTarifa() {
        if (getTipoTarifa() != null) {
            if (getTipoTarifa().equals(Util.TipoTarifa.H.toString())) {
                indTarifa = 'H';
            } else if (getTipoTarifa().equals(Util.TipoTarifa.P.toString())) {
                indTarifa = 'P';
            }
        }
        return indTarifa;
    }

    /**
     * @param indTarifa the indTarifa to set
     */
    public void setIndTarifa(char indTarifa) {
        this.indTarifa = indTarifa;
    }

    /**
     * @return the tarifaHabitacion
     */
    public TarifaHabitacion getTarifaHabitacion() {
        if (requestedPaqueteById != null) {
            if (requestedPaqueteById.getIdTipoPaqueteInt() != null || requestedPaqueteById.getIdTipoPaqueteNac() != null) {
                if (requestedPaqueteById.getIdTipoPaqueteInt() != null) {
                    tarifaHabitacion = requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaHabitacion() != null
                            ? requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaHabitacion() : null;
                } else {
                    tarifaHabitacion = requestedPaqueteById.getIdTipoPaqueteNac().getIdTarifaHabitacion() != null
                            ? requestedPaqueteById.getIdTipoPaqueteNac().getIdTarifaHabitacion() : null;
                }
            }
        }
        return tarifaHabitacion;
    }

    /**
     * @param tarifaHabitacion the tarifaHabitacion to set
     */
    public void setTarifaHabitacion(TarifaHabitacion tarifaHabitacion) {
        this.tarifaHabitacion = tarifaHabitacion;
    }

    /**
     * @return the tarifaPaquete
     */
    public TarifaPaquete getTarifaPaquete() {
        if (requestedPaqueteById != null) {
            if (requestedPaqueteById.getIdTipoPaqueteInt() != null || requestedPaqueteById.getIdTipoPaqueteNac() != null) {
                if (requestedPaqueteById.getIdTipoPaqueteInt() != null) {
                    tarifaPaquete = requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaPaquete() != null
                            ? requestedPaqueteById.getIdTipoPaqueteInt().getIdTarifaPaquete() : null;
                } else {
                    tarifaPaquete = requestedPaqueteById.getIdTipoPaqueteNac().getIdTarifaPaquete() != null
                            ? requestedPaqueteById.getIdTipoPaqueteNac().getIdTarifaPaquete() : null;
                }
            }
        }
        return tarifaPaquete;
    }

    /**
     * @param tarifaPaquete the tarifaPaquete to set
     */
    public void setTarifaPaquete(TarifaPaquete tarifaPaquete) {
        this.tarifaPaquete = tarifaPaquete;
    }

    /**
     * @return the tipoPaqueteInt
     */
    public TipoPaqueteInt getTipoPaqueteInt() {
        if (requestedPaqueteById != null && requestedPaqueteById.getIdTipoPaqueteInt() != null) {
            tipoPaqueteInt = requestedPaqueteById.getIdTipoPaqueteInt();
        }
        return tipoPaqueteInt;
    }

    /**
     * @param tipoPaqueteInt the tipoPaqueteInt to set
     */
    public void setTipoPaqueteInt(TipoPaqueteInt tipoPaqueteInt) {
        this.tipoPaqueteInt = tipoPaqueteInt;
    }

    /**
     * @return the tipoPaqueteNac
     */
    public TipoPaqueteNac getTipoPaqueteNac() {
        if (requestedPaqueteById != null && requestedPaqueteById.getIdTipoPaqueteNac() != null) {
            tipoPaqueteNac = requestedPaqueteById.getIdTipoPaqueteNac();
        }
        return tipoPaqueteNac;
    }

    /**
     * @param tipoPaqueteNac the tipoPaqueteNac to set
     */
    public void setTipoPaqueteNac(TipoPaqueteNac tipoPaqueteNac) {
        this.tipoPaqueteNac = tipoPaqueteNac;
    }

    /**
     * @return the idPaquete
     */
    public int getIdPaquete() {
        return idPaquete;
    }

    /**
     * @param idPaquete the idPaquete to set
     */
    public void setIdPaquete(int idPaquete) {
        this.idPaquete = idPaquete;
    }
}
