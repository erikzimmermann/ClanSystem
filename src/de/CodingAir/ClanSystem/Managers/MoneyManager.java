package de.CodingAir.ClanSystem.Managers;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class MoneyManager {
	
	public boolean isEssentialsEnabled() {
		try {
			Economy.getMoneyExact("IsEssentialsEnabled?");
		} catch(UserDoesNotExistException e) {
			return true;
		} catch(Exception e) {
			return false;
		}

		return true;
	}
	
}
