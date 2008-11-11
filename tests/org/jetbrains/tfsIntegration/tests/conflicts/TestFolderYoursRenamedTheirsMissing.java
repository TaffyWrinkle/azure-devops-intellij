/*
 * Copyright 2000-2008 JetBrains s.r.o.
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

package org.jetbrains.tfsIntegration.tests.conflicts;

import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.ChangeType;
import org.jetbrains.tfsIntegration.core.tfs.EnumMask;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.stubs.versioncontrol.repository.Conflict;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestFolderYoursRenamedTheirsMissing extends TestFolderConflict {

  private FilePath myBaseFolder;
  private FilePath myYoursFolder;

  protected boolean canMerge() {
    return false;
  }

  protected void preparePaths() {
    myBaseFolder = getChildPath(mySandboxRoot, BASE_FOLDERNAME);
    myYoursFolder = getChildPath(mySandboxRoot, YOURS_FOLDERNAME);
  }

  protected void prepareBaseRevision() {
    createDirInCommand(myBaseFolder);
  }

  protected void prepareTargetRevision() throws VcsException, IOException {
    deleteFileInCommand(myBaseFolder);
  }

  protected void makeLocalChanges() throws IOException, VcsException {
    rename(myBaseFolder, YOURS_FOLDERNAME);
  }

  protected void checkResolvedYoursState() throws VcsException {
    getChanges().assertTotalItems(1);
    getChanges().assertRenamedOrMoved(myBaseFolder, myYoursFolder);

    assertFolder(mySandboxRoot, 1);
    assertFolder(myYoursFolder, 0);
  }

  protected void checkResolvedTheirsState() throws VcsException {
    getChanges().assertTotalItems(0);

    assertFolder(mySandboxRoot, 0);
  }

  protected void checkResolvedMergeState() throws VcsException {
    Assert.fail("can't merge");
  }

  protected void checkConflictProperties(final Conflict conflict) throws TfsException {
    Assert.assertTrue(EnumMask.fromString(ChangeType.class, conflict.getYchg()).containsOnly(ChangeType.Rename));
    Assert.assertTrue(EnumMask.fromString(ChangeType.class, conflict.getBchg()).containsOnly(ChangeType.Delete));
    Assert.assertEquals(VersionControlPath.toSystemDependent(myYoursFolder), VersionControlPath.toSystemDependent(conflict.getSrclitem()));
    Assert.assertNull(conflict.getTgtlitem());
    Assert.assertEquals(findServerPath(myYoursFolder), conflict.getYsitem());
    Assert.assertEquals(findServerPath(myYoursFolder), conflict.getYsitemsrc());
    Assert.assertEquals(findServerPath(myBaseFolder), conflict.getBsitem());
    Assert.assertEquals(findServerPath(myBaseFolder), conflict.getTsitem());
  }

  @Nullable
  protected String mergeName() throws TfsException {
    Assert.fail("not supported");
    return null;
  }

  @Nullable
  protected String mergeContent() {
    Assert.fail("not supported");
    return null;
  }

  @Test
  public void testAcceptYours() throws VcsException, IOException {
    super.testAcceptYours();
  }

  @Test
  public void testAcceptTheirs() throws VcsException, IOException {
    super.testAcceptTheirs();
  }

  @Test
  public void testAcceptMerge() throws VcsException, IOException {
    super.testAcceptMerge();
  }
}