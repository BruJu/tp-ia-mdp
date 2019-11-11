package agent.planningagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import environnement.Action;
import environnement.Etat;
import environnement.IllegalActionException;
import environnement.MDP;
import utilitaire.UtilsMDP;
import environnement.Action2D;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration 
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
	/**
	 * discount facteur
	 */
	protected double gamma;

	/**
	 * fonction de valeur des etats
	 */
	protected HashMap<Etat,Double> V;
	
	/**
	 * 
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma,  MDP mdp) {
		super(mdp);
		this.gamma = gamma;
		
		this.V = new HashMap<Etat,Double>();
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
	}
	
	
	
	
	public ValueIterationAgent(MDP mdp) {
		this(0.9,mdp);
	}
	
	/**
	 * 
	 * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
	 * et notifie ses observateurs.
	 * Ce n'est pas la version inplace (qui utilise la nouvelle valeur de V pour mettre a jour ...)
	 */
	@Override
	public void updateV(){
		//delta est utilise pour detecter la convergence de l'algorithme
		//Dans la classe mere, lorsque l'on planifie jusqu'a convergence, on arrete les iterations        
		//lorsque delta < epsilon 
		//Dans cette classe, il  faut juste mettre a jour delta 
		this.delta=0.0;
		//*** VOTRE CODE
		
		HashMap<Etat, Double> nouveauV = new HashMap<>();
		
		for (Etat source : V.keySet()) {
			List<Action> actions = mdp.getActionsPossibles(source);
			
			if (actions.isEmpty()) {
				// Etat absorbant : recopie de l'ancien V
				nouveauV.put(source, V.get(source));
				continue;
			}
			
			double valeurActionMaximale = -Double.MAX_VALUE;

			// max
			for (Action action : actions) {
				try {
					// Somme pondérée de la valeur de chaque état atteignable par l'action
					double valeurAction = getSumFromValueIteration(source, action);

					// max
					if (valeurAction > valeurActionMaximale) {
						valeurActionMaximale = valeurAction;
					}
				} catch (IllegalActionException illegalAction) {
					// Do nothing
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			nouveauV.put(source, valeurActionMaximale);
			
			// Calcul de delta
			double diff = Math.abs(valeurActionMaximale - V.get(source));
			if (this.delta < diff) {
				this.delta = diff;
			}
		}
		
		// nouveauV est le nouveau V
		V = nouveauV;
		
		//mise a jour de vmax et vmin utilise pour affichage du gradient de couleur:
		//vmax est la valeur max de V pour tout s 
		//vmin est la valeur min de V pour tout s
		// ...
		
		vmin = Double.MAX_VALUE;
		vmax = -Double.MAX_VALUE;
		
		for (Double value : V.values()) {
			if (value < vmin) {
				vmin = value;
			}
			if (value > vmax) {
				vmax = value;
			}
		}
		
		
		//******************* laisser cette notification a la fin de la methode	
		this.notifyObs();
	}
	
	/**
	 * Renvoie la somme pondérée des valeurs des états suivants pour le couple etat, action
	 */
	private double getSumFromValueIteration(Etat source, Action action) throws Exception {
		double somme = 0.0;

		Map<Etat, Double> transitions = mdp.getEtatTransitionProba(source, action);
		
		for (Map.Entry<Etat, Double> destinationDouble : transitions.entrySet()) {
			Etat destination = destinationDouble.getKey();
			Double T = destinationDouble.getValue();
			Double R = mdp.getRecompense(source, action, destination);
			
			somme += T * (gamma * this.getValeur(destination) + R);
		}
		
		return somme;
	}

	/**
	 * Renvoie la somme pondérée des valeurs des états suivants pour le couple etat, action. Ne jète pas d'exception.
	 */
	private Double getSumFromValueIterationNoException(Etat source, Action action) {
		try {
			return getSumFromValueIteration(source, action);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * renvoi l'action executee par l'agent dans l'etat e 
	 * Si aucune actions possibles, renvoi Action2D.NONE
	 */
	@Override
	public Action getAction(Etat e) {
		//*** VOTRE CODE
		List<Action> actions = this.getPolitique(e);
		if (actions.size()==0)
			return Action2D.NONE;
		int r = rand.nextInt(actions.size());
		return actions.get(r);
	}


	@Override
	public double getValeur(Etat _e) {
                 //Renvoie la valeur de l'Etat _e, c'est juste un getter, ne calcule pas la valeur ici
                 //(la valeur est calculee dans updateV
		//*** VOTRE CODE
		return V.get(_e);
	}
	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans etat 
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		//*** VOTRE CODE
		
		// retourne action de meilleure valeur dans _e selon V, 
		// retourne liste vide si aucune action legale (etat absorbant)
		if (mdp.estAbsorbant(_e) || mdp.estBut(_e))
			return new ArrayList<>();
		
		return UtilsMDP.filterBest(mdp.getActionsPossibles(_e),
				action -> getSumFromValueIterationNoException(_e, action));
	}
	
	@Override
	public void reset() {
		super.reset();
                //reinitialise les valeurs de V
		//*** VOTRE CODE
		V.replaceAll((etat, ancienneValeur) -> 0.0);
		
		this.notifyObs();
	}

	
	public HashMap<Etat,Double> getV() {
		return V;
	}
	
	public double getGamma() {
		return gamma;
	}
	
	@Override
	public void setGamma(double _g){
		System.out.println("gamma= "+gamma);
		this.gamma = _g;
	}
}