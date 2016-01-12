package org.srb4j.webapp.fo.rest.support;

import io.swagger.jaxrs.config.BeanConfig;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.IOUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.srb4j.webapp.fo.rest.FrPlaceholder;
import org.srb4j.webapp.infrahelper.WebAppEnvProp;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class FoSwaggerJaxrsConfig extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6022464230486259162L;

	@Override
	public void init(ServletConfig servletConfig)
			throws javax.servlet.ServletException {

		super.init(servletConfig);

		WebApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext());
		WebAppEnvProp props = ctx.getBean(WebAppEnvProp.class);

		if (!props.isEnableSwagger()) {
			return;
		}

		String basePath = getResourceBasePath(servletConfig, props);
		BeanConfig swaggerConfig = new BeanConfig();

		String scheme = null;
		String host = null;

		String[] parts = basePath.split("://");
		if (parts.length > 1) {
			int pos = parts[1].indexOf("/");
			if (pos >= 0) {
				scheme = parts[0];
				basePath = parts[1].substring(pos);
				host = parts[1].substring(0, pos);
			} else {
				scheme = parts[0];
				basePath = null;
				host = parts[1];
			}
		}

		String title = "Your Backend System's Name";
		String email = "your-backend-developer@some-org.com";
		String desc = getDesc();

		swaggerConfig.setSchemes(new String[] { scheme });
		swaggerConfig.setTitle(title);
		swaggerConfig.setVersion("Your Version Number");
		swaggerConfig.setDescription(desc.toString());

		swaggerConfig.setContact(email);
		swaggerConfig.setHost(host);
		swaggerConfig.setBasePath(basePath);
		swaggerConfig.setPrettyPrint(true);
		swaggerConfig.setResourcePackage(FrPlaceholder.class.getPackage()
				.getName());
		swaggerConfig.setScan(true);

	}

	/**
	 * will not end with "/"
	 * 
	 * @param servletConfig
	 * @param props
	 * @return
	 */
	public static String getResourceBasePath(ServletConfig servletConfig,
			WebAppEnvProp props) {
		String contextPath = servletConfig.getServletContext().getContextPath();
		if(contextPath.startsWith("/")){
			contextPath = contextPath.substring(1);
		}
		if(contextPath.endsWith("/")){
			contextPath = contextPath.substring(0, contextPath.length() - 1);
		}
		return props.getSchemeAndHost()
				+ contextPath
				+ "/fo/rest";
	}

	private String getDesc() {
		try {
			return IOUtils.toString(FoSwaggerJaxrsConfig.class
					.getResourceAsStream("/swagger/desc.markdown"), "utf8");
		} catch (IOException e) {
			return null;
		}
	}

}
