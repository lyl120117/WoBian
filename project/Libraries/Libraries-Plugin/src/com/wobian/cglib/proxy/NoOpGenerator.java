/*
 * Copyright 2003,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wobian.cglib.proxy;

import com.wobian.cglib.core.ClassEmitter;
import com.wobian.cglib.core.CodeEmitter;
import com.wobian.cglib.core.EmitUtils;
import com.wobian.cglib.core.MethodInfo;
import com.wobian.cglib.core.TypeUtils;

import java.util.Iterator;
import java.util.List;
import net.sf.cglib.core.*;

class NoOpGenerator
implements CallbackGenerator
{
    public static final NoOpGenerator INSTANCE = new NoOpGenerator();

    public void generate(ClassEmitter ce, Context context, List methods) {
        for (Iterator it = methods.iterator(); it.hasNext();) {
            MethodInfo method = (MethodInfo)it.next();
            if (TypeUtils.isBridge(method.getModifiers()) || (
                    TypeUtils.isProtected(context.getOriginalModifiers(method)) &&
                    TypeUtils.isPublic(method.getModifiers()))) {
                CodeEmitter e = EmitUtils.begin_method(ce, method);
                e.load_this();
                context.emitLoadArgsAndInvoke(e, method);
                e.return_value();
                e.end_method();
            }
        }
    }
    
    public void generateStatic(CodeEmitter e, Context context, List methods) { }
}
