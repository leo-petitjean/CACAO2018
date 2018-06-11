package abstraction.eq4TRAN.VendeurChoco;

import java.util.ArrayList;
import java.util.Scanner;

import abstraction.eq4TRAN.IVendeurChoco;
import abstraction.fourni.Journal;

/**
 * 
 * @author Etienne
 *
 */
public class Vendeur implements IVendeurChoco{
	/*
	 * classe définissant les méthodes nécessaires à l'interface IVendeur Choco
	 */
	
	private double qBonbonBQ;
	private double qBonbonMQ;
	private double qBonbonHQ;
	private double qTabletteBQ;
	private double qTabletteMQ;
	private double qTabletteHQ;
	public Journal ventes = new Journal("ventes");
	
	public Vendeur(double qBBQ, double qBMQ, double qBHQ, double qTBQ, double qTMQ, double qTHQ) {
		qBonbonBQ = (qBBQ>=0) ? qBBQ : 0;
		qBonbonMQ = (qBMQ>=0) ? qBMQ : 0;
		qBonbonHQ = (qBHQ>=0) ? qBHQ : 0;
		qTabletteBQ = (qTBQ>=0) ? qTBQ : 0;
		qTabletteMQ = (qTMQ>=0) ? qTMQ : 0;
		qTabletteHQ = (qTHQ>=0) ? qTHQ : 0;
	}
	
	public Vendeur() {
		qBonbonBQ=0;
		qBonbonMQ=0;
		qBonbonHQ=0;
		qTabletteBQ=0;
		qTabletteMQ=0;
		qTabletteHQ=0;
	}

	public double getqBonbonBQ() {
		return qBonbonBQ;
	}

	public void setqBonbonBQ(int qBonbonBQ) {
		this.qBonbonBQ = qBonbonBQ;
	}

	public double getqBonbonMQ() {
		return qBonbonMQ;
	}

	public void setqBonbonMQ(int qBonbonMQ) {
		this.qBonbonMQ = qBonbonMQ;
	}

	public double getqBonbonHQ() {
		return qBonbonHQ;
	}

	public void setqBonbonHQ(int qBonbonHQ) {
		this.qBonbonHQ = qBonbonHQ;
	}

	public double getqTabletteBQ() {
		return qTabletteBQ;
	}

	public void setqTabletteBQ(int qTabletteBQ) {
		this.qTabletteBQ = qTabletteBQ;
	}

	public double getqTabletteMQ() {
		return qTabletteMQ;
	}

	public void setqTabletteMQ(int qTabletteMQ) {
		this.qTabletteMQ = qTabletteMQ;
	}

	public double getqTabletteHQ() {
		return qTabletteHQ;
	}

	public void setqTabletteHQ(int qTabletteHQ) {
		this.qTabletteHQ = qTabletteHQ;
	}

	public GQte getStock() {
		return new GQte(getqBonbonBQ(), getqBonbonMQ(), getqBonbonHQ(), getqTabletteBQ(), getqTabletteMQ(), getqTabletteHQ());
	}
	
	public GPrix getPrix() {
		ArrayList<Double[]> intervalles = new ArrayList<>();
		Double[] interval = {0.0,10.0,50.0,100.0,250.0,500.0,750.0,1000.0};
		for(int i=0;i<6;i++) {
			intervalles.add(interval);
		}
		ArrayList<Double[] > prix = new ArrayList<>();
		//Discuter de la stratégie d'etagement des prix
		Double[] prix4 = {0.72, 0.695, 0.650, 0.625, 0.6, 0.575, 0.55, 0.525};
		Double[] prix5 = {1.12, 1.1, 1.075, 1.05, 1.025, 1.0, 0.975, 0.95};
		Double[] prix6 = {2.0, 1.975, 1.95, 1.9, 1.875, 1.85, 1.825, 1.8};
		Double[] prix1 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		Double[] prix2 = {4.0, 3.975, 3.95, 3.9, 3.875, 3.85, 3.825, 3.8};
		Double[] prix3 = {6.4, 6.375, 6.35, 6.325, 6.3, 6.275, 6.25, 6.2};
		prix.add(prix1);
		prix.add(prix2);
		prix.add(prix3);
		prix.add(prix4);
		prix.add(prix5);
		prix.add(prix6);
		return new GPrix(intervalles, prix);
	}
	
	
	public ArrayList<GQte> getLivraison(ArrayList<GQte> commandes) {
		GQte commande1 = commandes.get(0);
		// On considère que commande1 correspond à la commande de l'équipe eq1DIST
		GQte commande2 = commandes.get(1);
		ArrayList<GQte> Livraison = new ArrayList<>(); /*insérer notre livraison effective */
		commande2.setqBonbonBQ(this.getqBonbonBQ()-commande1.getqBonbonBQ());
		commande2.setqBonbonMQ(this.getqBonbonMQ()-commande1.getqBonbonMQ());
		commande2.setqBonbonHQ(this.getqBonbonHQ()-commande1.getqBonbonHQ());
		commande2.setqTabletteBQ(this.getqTabletteBQ()-commande2.getqTabletteBQ());
		commande2.setqTabletteMQ(this.getqTabletteMQ()-commande2.getqTabletteMQ());
		commande2.setqTabletteHQ(this.getqTabletteHQ()-commande2.getqTabletteHQ());
		Livraison.add(commande1);
		Livraison.add(commande2);
		ventes.ajouter("Livraison : " + Livraison);
		return Livraison;
	}

}
