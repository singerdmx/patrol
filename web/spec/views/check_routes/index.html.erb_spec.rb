require 'spec_helper'

describe "check_routes/index" do
  before(:each) do
    assign(:check_routes, [
      stub_model(CheckRoute),
      stub_model(CheckRoute)
    ])
  end

  it "renders a list of check_routes" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
  end
end
