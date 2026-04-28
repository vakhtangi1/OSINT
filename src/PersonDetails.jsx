import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import "./App.css";

function PersonDetails() {
    const { id } = useParams();
    const [person, setPerson] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/api/person-records/${id}`)
            .then((res) => res.json())
            .then((data) => setPerson(data))
            .catch((err) => console.error("Error loading person:", err));
    }, [id]);

    if (!person) {
        return (
            <div className="container">
                <h1>Loading person...</h1>
            </div>
        );
    }

    return (
        <div className="container">
            <Link className="back-link" to={`/case/${person.caseId}`}>
                ← Back to Case
            </Link>

            <div className="person-page">
                {person.profilePhotoUrl ? (
                    <img
                        src={person.profilePhotoUrl}
                        alt={person.fullName}
                        className="person-big-photo"
                    />
                ) : (
                    <div className="person-big-placeholder">No Photo</div>
                )}

                <h1>{person.fullName}</h1>
                <p className="subtitle">{person.jobTitle}</p>

                <div className="details-grid">
                    <div>
                        <h3>First Name</h3>
                        <p>{person.firstName}</p>
                    </div>

                    <div>
                        <h3>Last Name</h3>
                        <p>{person.lastName}</p>
                    </div>

                    <div>
                        <h3>Company</h3>
                        <p>{person.company}</p>
                    </div>

                    <div>
                        <h3>Location</h3>
                        <p>{person.location}</p>
                    </div>

                    <div>
                        <h3>Email</h3>
                        <p>{person.email || "Not provided"}</p>
                    </div>

                    <div>
                        <h3>Phone</h3>
                        <p>{person.phoneNumber || "Not provided"}</p>
                    </div>

                    <div>
                        <h3>Source Type</h3>
                        <p>{person.sourceType}</p>
                    </div>

                    <div>
                        <h3>Confidence</h3>
                        <p>{person.confidenceScore}</p>
                    </div>

                    <div>
                        <h3>Created At</h3>
                        <p>{person.createdAt}</p>
                    </div>
                </div>

                {person.sourceUrl && (
                    <div className="description-box">
                        <h2>Source URL</h2>
                        <a href={person.sourceUrl} target="_blank" rel="noreferrer">
                            {person.sourceUrl}
                        </a>
                    </div>
                )}

                <div className="description-box">
                    <h2>Collected Public Text / Summary</h2>
                    <p>{person.collectedText || "No collected text saved."}</p>
                </div>

                <div className="description-box">
                    <h2>Analyst Notes</h2>
                    <p>{person.notes || "No notes saved."}</p>
                </div>
            </div>
        </div>
    );
}

export default PersonDetails;