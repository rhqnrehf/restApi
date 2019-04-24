package io.onshipping.demo.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)


public class WebMvcTest {
		@Test
		@Parameters(method="parametersForTestFree")
		public void testFree(int basePrice,int maxPrice,boolean isFree) {
			Event event = Event.builder()
					.basePrice(basePrice)
					.maxPrice(maxPrice)
					.build();
			event.update();
			
			assertThat(event.isFree()).isEqualTo(isFree);
		
		}

		private Object[] parametersForTestFree() {
			return new Object[] {
					new Object[] {0,0,true},
					new Object[] {100,0,false},
					new Object[] {0,100,false},
					new Object[] {100,200,false}
				
			};
		}
		@Test
		@Parameters
		public void testOffline(String location,boolean isOffLine) {
			Event event = Event.builder()
					.location(location)
					.build();
			
			event.update();
			
			assertThat(event.isOffLine()).isEqualTo(isOffLine);

		}
		
		private Object[] parametersForTestOffline() {
			return new Object[] {
				new Object[] {"강남",true},
				new Object[] {null,false},
				new Object[] {"",false}
			};
			
		}
}
