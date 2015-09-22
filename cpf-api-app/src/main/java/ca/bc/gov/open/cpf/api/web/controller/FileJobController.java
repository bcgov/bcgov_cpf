/*
 * Copyright © 2008-2015, Province of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.bc.gov.open.cpf.api.web.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;

import org.slf4j.LoggerFactory;

import ca.bc.gov.open.cpf.api.domain.CpfDataAccessObject;
import ca.bc.gov.open.cpf.api.scheduler.BatchJobService;

import com.revolsys.format.csv.CsvRecordWriter;
import com.revolsys.io.FileUtil;
import com.revolsys.record.Record;
import com.revolsys.record.schema.RecordDefinition;

public class FileJobController extends AbstractJobController {

  private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols();

  static {
    DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator('/');
  }

  public static String toPath(final long id) {
    final StringBuilder path = new StringBuilder();
    if (id < 0) {
      path.append('-');
    }
    final int numGroups;
    if (id == 0) {
      numGroups = 1;
    } else {
      numGroups = (int)Math.ceil((Math.floor(Math.log10(id)) + 1) / 3);
    }
    path.append(numGroups);
    path.append('/');
    final DecimalFormat decimalFormat = new DecimalFormat();
    decimalFormat.setMinimumIntegerDigits(numGroups * 3);
    decimalFormat.setDecimalFormatSymbols(DECIMAL_FORMAT_SYMBOLS);
    path.append(decimalFormat.format(id));
    return path.toString();
  }

  private final File rootDirectory;

  private final CpfDataAccessObject dataAccessObject;

  public FileJobController(final BatchJobService batchJobService, final File rootDirectory) {
    this.dataAccessObject = batchJobService.getDataAccessObject();
    this.rootDirectory = rootDirectory;
  }

  @Override
  public boolean cancelJob(final long jobId) {
    final boolean cancelled = this.dataAccessObject.cancelBatchJob(jobId);
    for (final String directoryName : Arrays.asList(JOB_INPUTS, JOB_RESULTS, GROUP_INPUTS,
      GROUP_RESULTS)) {
      final File directory = getJobDirectory(jobId, directoryName);
      deleteDirectory(jobId, directory);
    }
    return cancelled;
  }

  @Override
  public void createJobFile(final long jobId, final String path, final long sequenceNumber,
    final String contentType, final Object data) {
    final File file = getJobFile(jobId, path, sequenceNumber);
    file.getParentFile().mkdirs();
    if (data instanceof File) {
      final File dataFile = (File)data;
      FileUtil.copy(dataFile, file);
    } else if (data instanceof InputStream) {
      final InputStream in = (InputStream)data;
      FileUtil.copy(in, file);
    } else if (data instanceof byte[]) {
      final byte[] bytes = (byte[])data;
      FileUtil.copy(new ByteArrayInputStream(bytes), file);
    } else if (data instanceof String) {
      final String string = (String)data;
      FileUtil.copy(string, file);
    }
  }

  protected void deleteDirectory(final long jobId, final File directory) {
    if (!FileUtil.deleteDirectory(directory)) {
      LoggerFactory.getLogger(getClass())
        .error("Unable to delete  " + directory + " for jobId=" + jobId);
    }
  }

  @Override
  public void deleteJob(final long jobId) {
    this.dataAccessObject.deleteBatchJob(jobId);
    final File jobDirectory = getJobDirectory(jobId);
    deleteDirectory(jobId, jobDirectory);
  }

  @Override
  protected long getFileSize(final long jobId, final String path, final int sequenceNumber) {
    final File resultFile = getJobFile(jobId, path, sequenceNumber);
    return resultFile.length();
  }

  @Override
  protected InputStream getFileStream(final long jobId, final String path,
    final int sequenceNumber) {
    final File file = getJobFile(jobId, path, sequenceNumber);
    if (file.exists()) {
      return FileUtil.getInputStream(file);
    } else {
      return null;
    }
  }

  protected File getJobDirectory(final long jobId) {
    final File jobDirectory = FileUtil.getFile(this.rootDirectory, "jobs/" + toPath(jobId));
    return jobDirectory;
  }

  protected File getJobDirectory(final long jobId, final String path) {
    final File jobDirectory = getJobDirectory(jobId);
    final File groupsFile = FileUtil.getFile(jobDirectory, path);
    return groupsFile;
  }

  protected File getJobFile(final long jobId, final String path, final long recordId) {
    final File groupsFile = getJobDirectory(jobId, path);
    final File file = FileUtil.getFile(groupsFile, toPath(recordId) + ".json");
    return file;
  }

  @Override
  public InputStream getJobInputStream(final long jobId) {
    return getFileStream(jobId, JOB_INPUTS, 1);
  }

  @Override
  public InputStream getJobResultStream(final long jobId, final int sequenceNumber) {
    final File resultFile = getJobFile(jobId, JOB_RESULTS, sequenceNumber);
    return FileUtil.getInputStream(resultFile);
  }

  @Override
  public String getKey() {
    return "file";
  }

  @Override
  public void setGroupInput(final long jobId, final int sequenceNumber,
    final RecordDefinition recordDefinition, final List<Record> requests) {
    if (!requests.isEmpty()) {
      final File file = getJobFile(jobId, GROUP_INPUTS, sequenceNumber);
      file.getParentFile().mkdirs();
      try (
        CsvRecordWriter writer = new CsvRecordWriter(recordDefinition,
          FileUtil.createUtf8Writer(file), ',', true, false)) {
        for (final Record record : requests) {
          writer.write(record);
        }
      }
    }
  }
}
