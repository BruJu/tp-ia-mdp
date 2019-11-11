package agent.rlapproxagent;

import java.util.*;

import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
import javafx.util.Pair;
/**
 * Vecteur de fonctions caracteristiques phi_i(s,a): autant de fonctions caracteristiques que de paire (s,a),
 * <li> pour chaque paire (s,a), un seul phi_i qui vaut 1  (vecteur avec un seul 1 et des 0 sinon).
 * <li> pas de biais ici 
 * 
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionIdentity implements FeatureFunction {
	//*** VOTRE CODE
	private Map<Pair<Etat, Action>, double[]> map;
	private final int nbFeatures;
	
	public FeatureFunctionIdentity(int _nbEtat, int _nbAction){
		//*** VOTRE CODE
		map = new HashMap<>();
		this.nbFeatures = _nbEtat * _nbAction;
	}
	
	@Override
	public int getFeatureNb() {
		//*** VOTRE CODE
		return nbFeatures;
	}

	@Override
	public double[] getFeatures(Etat e,Action a){
		//*** VOTRE CODE

		Pair<Etat, Action> p = new Pair<>(e, a);
		if (map.containsKey(p)) {
			return map.get(p);
		} else {
			double[] features = new double[nbFeatures];
			features[map.keySet().size()] = 1;
			map.put(p, features);
			return features;
		}
	}
}
