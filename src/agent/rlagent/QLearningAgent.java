package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
import utilitaire.Pair;
import utilitaire.UtilsMDP;
/**
 * Renvoi 0 pour valeurs initiales de Q
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
	/**
	 *  format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
	 */
	protected HashMap<Pair<Etat,Action>,Double> qvaleurs;

	
	/**
	 * 
	 * @param alpha
	 * @param gamma
	 * @param Environnement
	 */
	public QLearningAgent(double alpha, double gamma,
			Environnement _env) {
		super(alpha, gamma,_env);
		qvaleurs = new HashMap<Pair<Etat, Action>, Double>();
	}


	
	
	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e
	 *  (plusieurs actions sont renvoyees si valeurs identiques)
	 *  renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)

	 */
	@Override
	public List<Action> getPolitique(Etat e) {
		// retourne action de meilleures valeurs dans e selon Q : utiliser getQValeur()
		// retourne liste vide si aucune action legale (etat terminal)
		List<Action> returnactions = getActionsLegales(e);
		if (this.getActionsLegales(e).size() == 0){//etat  absorbant; impossible de le verifier via environnement
			System.out.println("aucune action legale");
			return new ArrayList<Action>();
			
		}
		
		//*** VOTRE CODE
		return UtilsMDP.filterBest(returnactions, action -> getQValeur(e, action));
	}
	
	@Override
	public double getValeur(Etat e) {
		//*** VOTRE CODE
		List<Action> actionsLegales = getActionsLegales(e);
		
		Double max = null;
		
		for (Action action : actionsLegales) {
			double thisValue = getQValeur(e, action);
			
			if (max == null || thisValue > max) {
				max = thisValue;
			}
		}
		
		return max == null ? 0.0 : max;
	}

	@Override
	public double getQValeur(Etat e, Action a) {
		//*** VOTRE CODE
		return qvaleurs.getOrDefault(new Pair<>(e, a), 0.0);
	}
	
	
	
	@Override
	public void setQValeur(Etat e, Action a, double d) {
		//*** VOTRE CODE
		Pair<Etat, Action> cle = new Pair<>(e, a);
		qvaleurs.put(cle, d);
		
		// mise a jour vmax et vmin pour affichage du gradient de couleur:
				//vmax est la valeur de max pour tout s de V
				//vmin est la valeur de min pour tout s de V
				// ...
		
		List<Double> toutesLesValeursDesEtats = qvaleurs.keySet().stream()
				.map(paire -> paire.getLeft()) // Récupération des états
				.map(this::getValeur) // Récupération de leur valeur
				.sorted() // Tri
				.collect(Collectors.toList()); // Sous forme de liste
		
		
		if (toutesLesValeursDesEtats.isEmpty()) {
			vmin = 0;
			vmax = 0;
		} else {
			vmin = toutesLesValeursDesEtats.get(0);
			vmax = toutesLesValeursDesEtats.get(toutesLesValeursDesEtats.size() - 1);
		}
		
		this.notifyObs();
	}
	
	
	/**
	 * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
	 * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 * @param e
	 * @param a
	 * @param esuivant
	 * @param reward
	 */
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL)
			System.out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);

		//*** VOTRE CODE
		double nouveauq = getQValeur(e, a) * (1 - alpha) + alpha * (reward + gamma * getValeur(esuivant));
		setQValeur(e, a, nouveauq);
	}

	@Override
	public Action getAction(Etat e) {
		this.actionChoisie = this.stratExplorationCourante.getAction(e);
		return this.actionChoisie;
	}

	@Override
	public void reset() {
		super.reset();
		//*** VOTRE CODE
		qvaleurs.clear();
		vmin = 0.0;
		vmax = 0.0;
		
		
		this.episodeNb =0;
		this.notifyObs();
	}









	


}
