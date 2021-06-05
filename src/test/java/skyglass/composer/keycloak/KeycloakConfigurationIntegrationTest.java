package skyglass.composer.keycloak;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springboot.client.KeycloakSecurityContextClientRequestInterceptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class KeycloakConfigurationIntegrationTest {

	@Spy
	private KeycloakSecurityContextClientRequestInterceptor factory;

	private MockHttpServletRequest servletRequest;

	@Mock
	public KeycloakSecurityContext keycloakSecurityContext;

	@Mock
	private KeycloakPrincipal keycloakPrincipal;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		servletRequest = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));
		servletRequest.setUserPrincipal(keycloakPrincipal);
		when(keycloakPrincipal.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
	}

	@Test
	public void testGetKeycloakSecurityContext() throws Exception {
		assertNotNull(keycloakPrincipal.getKeycloakSecurityContext());
	}

}
