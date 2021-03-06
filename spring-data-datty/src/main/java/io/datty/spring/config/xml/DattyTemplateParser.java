/*
 * Copyright (C) 2016 Datty.io Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.datty.spring.config.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import io.datty.spring.config.DattyConfigConstants;
import io.datty.spring.config.DattyTemplateFactoryBean;

/**
 * DattyTemplateParser
 * 
 * @author Alex Shvid
 *
 */

public class DattyTemplateParser extends AbstractSimpleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return DattyTemplateFactoryBean.class;
	}

	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		String id = super.resolveId(element, definition, parserContext);
		return StringUtils.hasText(id) ? id : DattyConfigConstants.DATTY_TEMPLATE_DEFAULT_ID;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		String dattyRef = element.getAttribute("datty-ref");
		if (!StringUtils.hasText(dattyRef)) {
			dattyRef = DattyConfigConstants.DATTY_DEFAULT_ID;
		}
		builder.addPropertyReference("datty", dattyRef);

		String converterRef = element.getAttribute("datty-converter-ref");
		if (!StringUtils.hasText(converterRef)) {
			converterRef = DattyConfigConstants.DATTY_CONVERTER_DEFAULT_ID;
		}
		builder.addPropertyReference("converter", converterRef);

		postProcess(builder, element);
	}
}
