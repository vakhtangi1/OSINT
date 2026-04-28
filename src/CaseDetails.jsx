import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import "./App.css";

function CaseDetails() {
    const { id } = useParams();

    const [caseData, setCaseData] = useState(null);
    const [persons, setPersons] = useState([]);

    const emptyPersonForm = {
        caseId: id,
        firstName: "",
        lastName: "",
        fullName: "",
        jobTitle: "",
        company: "",
        location: "",
        email: "",
        phoneNumber: "",
        profilePhotoUrl: "",
        sourceUrl: "",
        sourceType: "LINKEDIN",
        collectedText: "",
        notes: "",
        confidenceScore: 1,
    };

    const [personForm, setPersonForm] = useState(emptyPersonForm);

    useEffect(() => {
        loadCase();
        loadPersons();
    }, [id]);

    const loadCase = async () => {
        const res = await fetch(`http://localhost:8080/api/cases/${id}`);
        const data = await res.json();
        setCaseData(data);
    };

    const loadPersons = async () => {
        const res = await fetch(
            `http://localhost:8080/api/person-records/case/${id}`
        );
        const data = await res.json();
        setPersons(data);
    };

    const handleChange = (e) => {
        setPersonForm({
            ...personForm,
            [e.target.name]: e.target.value,
        });
    };

    const savePerson = async (e) => {
        e.preventDefault();

        await fetch("http://localhost:8080/api/person-records", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(personForm),
        });

        setPersonForm(emptyPersonForm);
        loadPersons();
    };

    const importPdf = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        await fetch(`http://localhost:8080/api/pdf-import/person/${id}`, {
            method: "POST",
            body: formData,
        });

        e.target.value = "";
        loadPersons();
    };

    const deletePerson = async (personId) => {
        const confirmed = window.confirm("Delete this person record?");
        if (!confirmed) return;

        await fetch(`http://localhost:8080/api/person-records/${personId}`, {
            method: "DELETE",
        });

        loadPersons();
    };

    if (!caseData) {
        return (
            <div className="container">
                <h1>Loading case...</h1>
            </div>
        );
    }

    return (
        <div className="container">
            <Link className="back-link" to="/">
                ← Back to Dashboard
            </Link>

            <div className="details-card">
                <h1>{caseData.title}</h1>

                <div className="details-grid">
                    <div>
                        <h3>Case ID</h3>
                        <p>{caseData.id}</p>
                    </div>

                    <div>
                        <h3>Status</h3>
                        <p>{caseData.status}</p>
                    </div>

                    <div>
                        <h3>Created At</h3>
                        <p>{caseData.createdAt}</p>
                    </div>
                </div>

                <div className="description-box">
                    <h2>Case Description</h2>
                    <p>{caseData.description}</p>
                </div>
            </div>

            <div className="card">
                <h2>Import Person From PDF</h2>

                <div className="pdf-upload-box">
                    <input type="file" accept="application/pdf" onChange={importPdf} />
                    <p>Upload LinkedIn PDF, CV, or authorized public profile PDF.</p>
                </div>
            </div>

            <div className="card">
                <h2>Add Person Profile Manually</h2>

                <form className="person-form" onSubmit={savePerson}>
                    <input
                        name="firstName"
                        placeholder="First name"
                        value={personForm.firstName}
                        onChange={handleChange}
                    />

                    <input
                        name="lastName"
                        placeholder="Last name"
                        value={personForm.lastName}
                        onChange={handleChange}
                    />

                    <input
                        name="fullName"
                        placeholder="Full name"
                        value={personForm.fullName}
                        onChange={handleChange}
                        required
                    />

                    <input
                        name="jobTitle"
                        placeholder="Job title"
                        value={personForm.jobTitle}
                        onChange={handleChange}
                    />

                    <input
                        name="company"
                        placeholder="Company"
                        value={personForm.company}
                        onChange={handleChange}
                    />

                    <input
                        name="location"
                        placeholder="Location"
                        value={personForm.location}
                        onChange={handleChange}
                    />

                    <input
                        name="email"
                        placeholder="Email if public/authorized"
                        value={personForm.email}
                        onChange={handleChange}
                    />

                    <input
                        name="phoneNumber"
                        placeholder="Phone if public/authorized"
                        value={personForm.phoneNumber}
                        onChange={handleChange}
                    />

                    <input
                        name="profilePhotoUrl"
                        placeholder="Profile photo URL"
                        value={personForm.profilePhotoUrl}
                        onChange={handleChange}
                    />

                    <input
                        name="sourceUrl"
                        placeholder="Source URL"
                        value={personForm.sourceUrl}
                        onChange={handleChange}
                    />

                    <select
                        name="sourceType"
                        value={personForm.sourceType}
                        onChange={handleChange}
                    >
                        <option value="LINKEDIN">LINKEDIN</option>
                        <option value="GITHUB">GITHUB</option>
                        <option value="PUBLIC_WEBSITE">PUBLIC_WEBSITE</option>
                        <option value="PDF">PDF</option>
                        <option value="OTHER">OTHER</option>
                    </select>

                    <input
                        name="confidenceScore"
                        type="number"
                        step="0.1"
                        min="0"
                        max="1"
                        placeholder="Confidence"
                        value={personForm.confidenceScore}
                        onChange={handleChange}
                    />

                    <textarea
                        name="collectedText"
                        placeholder="Collected public text / summary"
                        value={personForm.collectedText}
                        onChange={handleChange}
                    />

                    <textarea
                        name="notes"
                        placeholder="Analyst notes"
                        value={personForm.notes}
                        onChange={handleChange}
                    />

                    <button type="submit">Save Person to Database</button>
                </form>
            </div>

            <div className="card">
                <h2>People Connected to This Case</h2>

                {persons.length === 0 ? (
                    <p className="empty">No person records saved yet.</p>
                ) : (
                    <div className="person-grid">
                        {persons.map((person) => (
                            <div className="person-card" key={person.id}>
                                <Link className="person-card-link" to={`/person/${person.id}`}>
                                    {person.profilePhotoUrl ? (
                                        <img src={person.profilePhotoUrl} alt={person.fullName} />
                                    ) : (
                                        <div className="avatar-placeholder">No Photo</div>
                                    )}

                                    <div>
                                        <h3>{person.fullName}</h3>
                                        <p>
                                            <strong>Job:</strong> {person.jobTitle || "Not provided"}
                                        </p>
                                        <p>
                                            <strong>Company:</strong>{" "}
                                            {person.company || "Not provided"}
                                        </p>
                                        <p>
                                            <strong>Location:</strong>{" "}
                                            {person.location || "Not provided"}
                                        </p>
                                        <p>
                                            <strong>Phone:</strong>{" "}
                                            {person.phoneNumber || "Not provided"}
                                        </p>
                                        <p>
                                            <strong>Source:</strong>{" "}
                                            {person.sourceType || "Not provided"}
                                        </p>
                                        <p>
                                            <strong>Confidence:</strong> {person.confidenceScore}
                                        </p>
                                    </div>
                                </Link>

                                <button
                                    className="delete-btn"
                                    onClick={() => deletePerson(person.id)}
                                >
                                    Delete Person
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default CaseDetails;