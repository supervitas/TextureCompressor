package Job;

import java.util.ArrayList;
import java.util.List;

public class JobManager {
    private ArrayList<JobCompress> jobs = new ArrayList<>();
    private int maxJobs = 4;

    public void CreateJobCompress(String folderWithTextures, int jobId) {
        JobCompress job = new JobCompress(jobId, folderWithTextures);
        jobs.add(job);
    }

    public boolean GetStatusOfJob(int jobId) {
        for (JobCompress job : jobs) {
            if (job.jobId == jobId){
                return job.GetStatusOfJob();
            }
        }
        return false;
    }

}
