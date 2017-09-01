package Job;

import java.util.ArrayList;
import java.util.List;

public class JobManager {
    private ArrayList<JobCompress> jobs = new ArrayList<>();
    private final int MAXJOBS = 6;
    private int currentJobsCount = 0;

    public boolean CreateJobCompress(String folderWithTextures, int jobId) {
        if (currentJobsCount < MAXJOBS) {
            currentJobsCount++;
            JobCompress job = new JobCompress(jobId, folderWithTextures, ()-> currentJobsCount--);
            jobs.add(job);

            return true;
        }
        return false;
    }

    public boolean GetStatusOfJob(int jobId) {
        for (JobCompress job : jobs) {
            if (job.jobId == jobId) {
                return job.GetStatusOfJob();
            }
        }
        return false;
    }

}
