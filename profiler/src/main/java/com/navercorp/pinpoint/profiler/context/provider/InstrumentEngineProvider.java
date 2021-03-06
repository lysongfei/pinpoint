/*
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.navercorp.pinpoint.bootstrap.AgentOption;
import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClassPool;

import com.navercorp.pinpoint.profiler.instrument.ASMClassPool;
import com.navercorp.pinpoint.profiler.instrument.JavassistClassPool;
import com.navercorp.pinpoint.profiler.interceptor.registry.InterceptorRegistryBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Woonduk Kang(emeroad)
 */
public class InstrumentEngineProvider implements Provider<InstrumentClassPool> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProfilerConfig profilerConfig;
    private final AgentOption agentOption;
    private final InterceptorRegistryBinder interceptorRegistryBinder;

    @Inject
    public InstrumentEngineProvider(ProfilerConfig profilerConfig, AgentOption agentOption, InterceptorRegistryBinder interceptorRegistryBinder) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (agentOption == null) {
            throw new NullPointerException("agentOption must not be null");
        }
        if (interceptorRegistryBinder == null) {
            throw new NullPointerException("interceptorRegistryBinder must not be null");
        }

        this.profilerConfig = profilerConfig;
        this.agentOption = agentOption;
        this.interceptorRegistryBinder = interceptorRegistryBinder;
    }

    public InstrumentClassPool get() {
        final String instrumentEngine = profilerConfig.getProfileInstrumentEngine().toUpperCase();

        if (DefaultProfilerConfig.INSTRUMENT_ENGINE_ASM.equals(instrumentEngine)) {
            logger.info("ASM InstrumentEngine.");

            return new ASMClassPool(interceptorRegistryBinder, agentOption.getBootstrapJarPaths());

        } else if (DefaultProfilerConfig.INSTRUMENT_ENGINE_JAVASSIST.equals(instrumentEngine)) {
            logger.info("JAVASSIST InstrumentEngine.");

            return new JavassistClassPool(interceptorRegistryBinder, agentOption.getBootstrapJarPaths());
        } else {
            logger.warn("Unknown InstrumentEngine:{}", instrumentEngine);

            throw new IllegalArgumentException("Unknown InstrumentEngine:" + instrumentEngine);
        }
    }
}
