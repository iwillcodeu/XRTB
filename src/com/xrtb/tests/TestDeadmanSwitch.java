package com.xrtb.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.xrtb.bidder.DeadmanSwitch;
import com.xrtb.common.Configuration;

public class TestDeadmanSwitch {

	@Test 
	public void testSwitch() throws Exception {
			Jedis redis = new Jedis("localhost");
			redis.connect();
			if (Configuration.getInstance().password != null)
				redis.auth(Configuration.getInstance().password);

			DeadmanSwitch.testmode = true;
			
			if (Configuration.setPassword() != null)
				redis.auth(Configuration.setPassword());
			
			redis.del("deadmanswitch");
			
			DeadmanSwitch d = new DeadmanSwitch("localhost",6379, "deadmanswitch");
			Thread.sleep(1000);
			assertFalse(d.canRun());
			redis.set("deadmanswitch", "ready");
			redis.expire("deadmanswitch", 5);
			
			assertTrue(d.canRun());
			Thread.sleep(10000);
			assertFalse(d.canRun());
		}
}
