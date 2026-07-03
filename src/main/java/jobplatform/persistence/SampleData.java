package jobplatform.persistence;

import jobplatform.model.*;
import jobplatform.model.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;

public class SampleData {

    private SampleData() {
    }

    public static void load() {
        if (!PlatformUser.getExtent().isEmpty()) {
            return;
        }

        Skill java = new Skill("Java", "Programming");
        Skill sql = new Skill("SQL", "Database");
        Skill swing = new Skill("Swing", "GUI");
        Skill ux = new Skill("UX Design", "Design");
        Skill projectManagement = new Skill("Project Management", "Management");

        Company techNova = new Company(
                "TechNova Solutions",
                "Software company focused on business applications.",
                "https://technova.example.com",
                "Warsaw"
        );
        techNova.setVerificationStatus("VERIFIED");

        Company designHub = new Company(
                "DesignHub Studio",
                "Digital design and product studio.",
                "https://designhub.example.com",
                "Krakow"
        );
        designHub.setVerificationStatus("VERIFIED");

        PlatformUser seekerUser = new PlatformUser(
                "oleksandr@example.com",
                "Oleksandr Hoshovskyi"
        );
        JobSeeker seeker = new JobSeeker(seekerUser, "Java Intern", "Warsaw");
        seeker.getCandidateProfile().completeMissingData(
                "Junior Java Developer",
                "Computer science student interested in Java, databases and desktop applications.",
                "Java Intern",
                "Warsaw"
        );
        CV seekerCV = new CV(seeker.getCandidateProfile(), "oleksandr_cv_java.pdf", true);
        new CandidateSkill(seeker.getCandidateProfile(), java, "Intermediate", 2);
        new CandidateSkill(seeker.getCandidateProfile(), sql, "Basic", 1);
        new CandidateSkill(seeker.getCandidateProfile(), swing, "Basic", 1);

        PlatformUser incompleteUser = new PlatformUser(
                "anna@example.com",
                "Anna Kowalska"
        );
        JobSeeker incompleteSeeker = new JobSeeker(incompleteUser, "", "");
        new CV(incompleteSeeker.getCandidateProfile(), "anna_cv.pdf", true);

        PlatformUser employerUser = new PlatformUser(
                "sarah@example.com",
                "Sarah Muller"
        );
        Employer employer = new Employer(employerUser, techNova, "HR Manager");

        PlatformUser secondEmployerUser = new PlatformUser(
                "michael@example.com",
                "Michael Nowak"
        );
        Employer secondEmployer = new Employer(secondEmployerUser, designHub, "Recruitment Lead");

        PlatformUser dualUser = new PlatformUser(
                "alex.manager@example.com",
                "Alex Manager"
        );
        JobSeeker dualSeekerRole = new JobSeeker(dualUser, "Project Coordinator", "Warsaw");
        dualSeekerRole.getCandidateProfile().completeMissingData(
                "Project Coordinator",
                "Candidate with experience in coordination and communication.",
                "Project Coordinator",
                "Warsaw"
        );
        new CV(dualSeekerRole.getCandidateProfile(), "alex_cv.pdf", true);
        new CandidateSkill(dualSeekerRole.getCandidateProfile(), projectManagement, "Advanced", 4);
        new Employer(dualUser, techNova, "Team Lead");

        RecruitmentProcess javaProcess = createRecruitmentProcess("Java Internship Recruitment");
        RecruitmentProcess uxProcess = createRecruitmentProcess("UX Recruitment");
        RecruitmentProcess freelanceProcess = createRecruitmentProcess("Freelance Project Recruitment");
        RecruitmentProcess workStuffProcess = createRecruitmentProcess("General IT Recruitment");

        JobOffer javaOffer = new PermanentOffer(
                employer,
                "Java Intern",
                "Internship position for a student who wants to work with Java and business applications.",
                "Warsaw",
                "Hybrid",
                "4000-5500 PLN",
                List.of("Basic Java knowledge", "Willingness to learn", "English communication"),
                javaProcess,
                30,
                true
        );
        javaOffer.publish();

        JobOffer uxOffer = new ContractOffer(
                secondEmployer,
                "Junior UX Designer",
                "Junior UX role focused on interface research, wireframes and usability improvements.",
                "Krakow",
                "Remote",
                "5000-7000 PLN",
                List.of("Portfolio", "Basic UX research knowledge", "Figma"),
                uxProcess,
                12,
                true
        );
        uxOffer.publish();

        JobOffer freelanceOffer = new FreelanceOffer(
                secondEmployer,
                "Landing Page Redesign",
                "Freelance project for redesigning a product landing page.",
                "Remote",
                "Remote",
                "3000-4500 PLN",
                List.of("Web design experience", "Responsive design", "Communication with client"),
                freelanceProcess,
                4500.0,
                true
        );
        freelanceOffer.publish();

        JobOffer workStuffOffer = new PermanentOffer(
                employer,
                "Work Stuff",
                "General entry-level IT work connected with support, documentation and simple development tasks.",
                "Warsaw",
                "On-site",
                "4500-6000 PLN",
                List.of("Basic IT knowledge", "Reliability", "Communication skills"),
                workStuffProcess,
                30,
                true
        );
        workStuffOffer.publish();

        seeker.saveJobOffer(javaOffer, "Good match for Java and Swing project experience.");
        seeker.saveJobOffer(workStuffOffer, "Already applied, but useful to keep for tracking.");

        Application existingApplication = seeker.submitApplication(
                workStuffOffer,
                seekerCV,
                "I am interested in this position because it matches my current Java and IT support skills."
        );
        existingApplication.updateStatus(ApplicationStatus.UNDER_REVIEW);

        Interview sampleInterview = new Interview(
                LocalDateTime.now().plusDays(5),
                "VIDEO"
        );
        existingApplication.scheduleInterview(sampleInterview);

        DataStore.save();
    }

    private static RecruitmentProcess createRecruitmentProcess(String name) {
        RecruitmentProcess process = new RecruitmentProcess(name);

        new RecruitmentStage(process, "CV Review", 1, "Screening", 5);
        new RecruitmentStage(process, "Interview", 2, "Interview", 7);
        new RecruitmentStage(process, "Decision", 3, "Decision", 3);

        process.startProcess();

        return process;
    }
}