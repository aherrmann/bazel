// Copyright 2014 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.rules.cpp;

import com.google.common.base.Preconditions;
import com.google.devtools.build.lib.analysis.BlazeDirectories;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.skyframe.SkyFunction;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import javax.annotation.Nullable;

/**
 * Wrapper for {@link FdoSupport} that turns it into a {@link SkyFunction}.
 *
 * <p>This only exists because the value of {@code --fdo_optimize} can be a workspace-relative path
 * and thus we need to depend on {@link BlazeDirectories} somehow, which neither the configuration
 * nor the analysis phase can "officially" do.
 *
 * <p>The fix is probably to make it possible for
 * {@link com.google.devtools.build.lib.analysis.actions.SymlinkAction} to create workspace-relative
 * symlinks because action execution can hopefully depend on {@link BlazeDirectories}.
 */
public class FdoSupportFunction implements SkyFunction {
  private final BlazeDirectories directories;

  public FdoSupportFunction(BlazeDirectories directories) {
    this.directories = Preconditions.checkNotNull(directories);
  }

  @Nullable
  @Override
  public SkyValue compute(SkyKey skyKey, Environment env) throws InterruptedException {
    FdoSupportValue.Key key = (FdoSupportValue.Key) skyKey.argument();
    Path fdoZip =
        key.getFdoZip() == null ? null : directories.getWorkspace().getRelative(key.getFdoZip());
    FdoSupport fdoSupport = new FdoSupport(fdoZip);
    return new FdoSupportValue(fdoSupport);
  }

  @Nullable
  @Override
  public String extractTag(SkyKey skyKey) {
    return null;
  }
}
