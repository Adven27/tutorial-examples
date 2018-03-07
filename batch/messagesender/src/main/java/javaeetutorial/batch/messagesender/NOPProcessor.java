/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * <p>
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.messagesender;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("NOPProcessor")
public class NOPProcessor implements ItemProcessor {

    public NOPProcessor() {
    }

    @Override
    public Object processItem(Object obj) throws Exception {
        return obj;
    }
}
