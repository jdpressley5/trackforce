package com.revature.test.dao;
import static org.junit.Assert.*;
import org.junit.Test;

import com.revature.daoimpl.AssociateDaoImpl;

public class DatabaseTester {

	@Test
	public void test() {
		AssociateDaoImpl impl = new AssociateDaoImpl();
		impl.getNAssociateMatchingCriteria(1, 3, 1, 5);
		fail("ugh");
	}

}
